package com.noovitec.mpb.repo.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Sale;

public interface CustomSaleRepo {
	public Page<?> findPageable(Pageable pageable, boolean totals, String saleNumber, Long itemId, Long customerId, String status, String customFilter, boolean showAll);

	@Repository
	public class CustomSaleRepoImpl implements CustomSaleRepo {

		private final Logger log = LoggerFactory.getLogger(CustomSaleRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		public Page<?> findPageable(Pageable pageable, boolean totals, String saleNumber, Long itemId, Long customerId, String status, String customFilter, boolean showAll) {
			List<Long> ids = this.getIds(pageable, saleNumber, itemId, customerId, status, customFilter, showAll);
			Query query = entityManager.createQuery("select count(*) from Sale s where s.id in :ids ");
			query.setParameter("ids", ids);
			long total = (long) query.getSingleResult();
			if(totals) {
				query = entityManager.createQuery("select sum(s.unitsSold)+sum(s.unitsAdjusted), sum(s.totalPrice) from Sale s where s.id in :ids ");
				query.setParameter("ids", ids);
				Object result = query.getSingleResult();
				Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			}else {
				String q = "select distinct s from Sale s where s.id in :ids ";
				Order order = pageable.getSort().iterator().next();
				q += "order by s."+order.getProperty() + " "+order.getDirection();
				query = entityManager.createQuery(q);
				query.setParameter("ids", ids);
				@SuppressWarnings("unchecked")
				List<Object> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				List<Sale> entities = new ArrayList<Sale>();
				result.forEach(o -> entities.add((Sale) o));
				Page<Sale> page = new PageImpl<Sale>(entities, pageable, total);
				return page;
			}
		}
		
		private List<Long> getIds(Pageable pageable, String saleNumber, Long itemId, Long customerId, String status, String customFilter, boolean showAll) {
			String q = "select distinct s.id from Sale s " 
					+ "left join s.saleItems si "
					+ "left join si.itemPackaging ip " 
					+ "left join ip.item i "
					+ "left join s.customer c " 
					+ "where s.id is not null ";
			if (saleNumber !=null && !saleNumber.isBlank()) {
				q += "and upper(s.number) like concat('%',upper(:saleNumber),'%') ";
			}
			if (itemId !=null) {
				q += "and i.id = :itemId ";
			}
			if (customerId !=null) {
				q += "and c.id = :customerId ";
			}
			if (status != null && !status.isBlank()) {
				q += "and s.status = :status ";
			}
			if (Sale.CUSTOM_FILTER.NOT_PAID.name().equalsIgnoreCase(customFilter)) {
				q += "and s.paidInFull = false ";
			}
			if (Sale.CUSTOM_FILTER.PC_NOT_READY.name().equalsIgnoreCase(customFilter)) {
				q += "and s.pcr = false ";
			}
			if (!showAll) {
				q += "and s.cancelled = false and s.paidInFull = false ";
			}
			Query query = entityManager.createQuery(q);
			if (saleNumber !=null && !saleNumber.isBlank()) {
				query.setParameter("saleNumber", saleNumber);
			}
			if (itemId !=null) {
				query.setParameter("itemId", itemId);
			}
			if (customerId !=null) {
				query.setParameter("customerId", customerId);
			}
			if (status !=null && !status.isBlank()) {
				query.setParameter("status", status);
			}
			@SuppressWarnings("unchecked")
			List<Long> ids = query.getResultList();
			if(ids.size()==0) {
				ids.add(0L);
			}
			return ids;
		}

	}
}