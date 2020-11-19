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
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;

public interface CustomScheduleEventRepo {
	Page<?> findPageable(Pageable pageable, boolean totals, Long saleId, Long itemId, Long packagingId);

	@Repository
	public class CustomScheduleEventRepoImpl implements CustomScheduleEventRepo {

		private final Logger log = LoggerFactory.getLogger(CustomScheduleEventRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPageable(Pageable pageable, boolean totals, Long saleId, Long itemId, Long packagingId) {
			List<Long> ids = this.getIds(pageable, totals, saleId, itemId, packagingId);
			Query query = entityManager.createQuery("select count(*) from ScheduleEvent se where se.id in :ids");
			query.setParameter("ids", ids);
			long total = (long) query.getSingleResult();
			if(totals) {
				query = entityManager.createQuery("select sum(se.unitsScheduled), sum(se.unitsProduced) from ScheduleEvent se where se.id in :ids ");
				query.setParameter("ids", ids);
				Object result = query.getSingleResult();
				Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			}else {
				String q = "select distinct se, line from ScheduleEvent se "
						+ "join se.line line "
						+ "where se.id in :ids ";
				q += "order by se.date desc, line.id asc ";
				query = entityManager.createQuery(q);
				query.setParameter("ids", ids);
				@SuppressWarnings("unchecked")
				List<Object[]> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				List<ScheduleEvent> entities = new ArrayList<ScheduleEvent>();
				result.forEach(o -> entities.add((ScheduleEvent) o[0]));
				Page<ScheduleEvent> page = new PageImpl<ScheduleEvent>(entities, pageable, total);
				return page;
			}			
		}
		
		private List<Long> getIds(Pageable pageable, boolean totals, Long saleId, Long itemId, Long packagingId) {
			String q = "select distinct se.id from ScheduleEvent se "
					+ "left join se.saleItem si "
					+ "left join si.sale s "
					+ "join se.itemPackaging ip "
					+ "join ip.packaging p "
					+ "join ip.item i " 
					+ "where se.id is not null ";
			if (saleId != null) {
				if(saleId == 0) {
					q += "and s.id is null ";
				} else {
					q += "and s.id = :saleId ";
				}
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			if (packagingId != null) {
				q += "and p.id = :packagingId ";
			}
			Query query = entityManager.createQuery(q);
			if (saleId != null && saleId != 0) {
				query.setParameter("saleId", saleId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (packagingId != null) {
				query.setParameter("packagingId", packagingId);
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