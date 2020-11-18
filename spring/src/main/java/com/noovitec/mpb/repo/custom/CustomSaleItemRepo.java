package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
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

import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.SaleItem;

public interface CustomSaleItemRepo {
	Page<?> findPageable(Pageable pageable, boolean totals, String numberName, Long saleId, Long customerId, Long itemId, 
			Long packagingId, String status, String unitsFilter, boolean showAll);

	@Repository
	public class SaleItemRepoImpl implements CustomSaleItemRepo {

		private final Logger log = LoggerFactory.getLogger(SaleItemRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		
		public Page<?> findPageable(Pageable pageable, boolean totals, String numberName, Long saleId, Long customerId, Long itemId, 
				Long packagingId, String status, String unitsFilter, boolean showAll) {
			List<Long> ids = this.getIds(pageable, numberName, saleId, customerId, itemId, packagingId, status, unitsFilter, showAll);
			Query query = entityManager.createQuery("select count(*) from SaleItem si where si.id in :ids");
			query.setParameter("ids", ids);
			long total = (long) query.getSingleResult();
			if(totals) {
				query = entityManager.createQuery("select (sum(si.units)+sum(si.unitsAdjusted)), sum(si.unitsProduced), sum(si.unitsAssigned), sum(si.unitsShipped) from SaleItem si where si.id in :ids ");
				query.setParameter("ids", ids);
				Object result = query.getSingleResult();
				Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			}else {
				String q = "select distinct si, i from SaleItem si "
						+ "join si.itemPackaging ip "
						+ "join ip.item i "
						+ "where si.id in :ids ";
//				Order order = pageable.getSort().iterator().next();
//				q += "order by si."+order.getProperty() + " "+order.getDirection();
				q += "order by si.updated desc, i.number asc";
				query = entityManager.createQuery(q);
				query.setParameter("ids", ids);
				@SuppressWarnings("unchecked")
				List<Object[]> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				List<SaleItem> entities = new ArrayList<SaleItem>();
				result.forEach(o -> entities.add((SaleItem) o[0]));
				Page<SaleItem> page = new PageImpl<SaleItem>(entities, pageable, total);
				return page;
			}
		}
		
		
		private List<Long> getIds(Pageable pageable, String numberName, Long saleId, Long customerId, Long itemId, 
				Long packagingId, String status, String unitsFilter, boolean showAll) {
			String q = "select distinct si.id from SaleItem si "
					+ "join si.itemPackaging ip " 
					+ "join ip.item i " 
					+ "join si.sale s " 
					+ "join s.customer cu "
					+ "join ip.packaging p " 
					+ "where si.id is not null ";
			if (numberName != null && !numberName.isEmpty()) {
				q += "and (upper(s.number) like concat('%',upper(:numberName),'%') ";
				q += "or upper(s.name) like concat('%',upper(:numberName),'%')) ";
			}
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}
			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			if (packagingId != null) {
				q += "and p.id = :packagingId ";
			}
			if (status !=null && !status.isBlank()) {
				q += "and si.status = :status ";
			}
//			if(unitsFilter != null) {
//				if(unitsFilter.equalsIgnoreCase("ON_FLOOR")) {
//					q += "and (si.unitsProduced - si.unitsShipped) != 0 ";
//				}
//				if(unitsFilter.equalsIgnoreCase("ON_STOCK")) {
//					q += "and si.unitsOnStock != 0 ";
//				}
//				if(unitsFilter.equalsIgnoreCase("NOT_ASSIGNED")) {
//					q += "and ((si.units + si.unitsAdjusted) - si.unitsAssigned) != 0 ";
//				}
//			}
			if (!showAll) {
				q += "and s.cancelled = false and s.paidInFull = false ";
			}
			Query query = entityManager.createQuery(q);
			if (numberName != null && !numberName.isEmpty()) {
				query.setParameter("numberName", numberName);
			}
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (packagingId != null) {
				query.setParameter("packagingId", packagingId);
			}
			if (status != null && !status.isBlank()) {
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