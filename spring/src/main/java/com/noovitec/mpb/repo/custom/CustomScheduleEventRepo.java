package com.noovitec.mpb.repo.custom;

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

import com.noovitec.mpb.entity.ScheduleEvent;

public interface CustomScheduleEventRepo {
	Page<?> findPageable(Pageable pageable, Long saleId, Long itemId, Long packagingId);

	@Repository
	public class CustomScheduleEventRepoImpl implements CustomScheduleEventRepo {

		private final Logger log = LoggerFactory.getLogger(CustomScheduleEventRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPageable(Pageable pageable, Long saleId, Long itemId, Long packagingId) {
			String q = "select distinct se from ScheduleEvent se "
					+ "left join se.saleItem si "
					+ "left join si.sale s "
					+ "join se.itemPackaging ip "
					+ "join ip.packaging p "
					+ "join ip.item i " 
					+ "where se.id is not null ";
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			if (packagingId != null) {
				q += "and p.id = :packagingId ";
			}
			q += "order by se.date desc, se.line.id asc ";
			Query query = entityManager.createQuery(q);
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (packagingId != null) {
				query.setParameter("packagingId", packagingId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<ScheduleEvent> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<ScheduleEvent> page = new PageImpl<ScheduleEvent>(result, pageable, total);
			return page;
		}
	}
}