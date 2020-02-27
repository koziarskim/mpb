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

import com.noovitec.mpb.entity.Component;

public interface CustomComponentRepo {
	Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId);

	@Repository
	public class CustomComponentRepoImpl implements CustomComponentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomComponentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId) {
			String q = "select c from Component c "
					+ "join c.supplier supplier "
					+ "join c.itemComponents ic "
					+ "where c.id is not null ";
			if (nameSearch != null && !nameSearch.isEmpty()) {
				q += "and (upper(c.number) like concat('%',upper(:nameSearch),'%') ";
				q += "or upper(c.name) like concat('%',upper(:nameSearch),'%')) ";
			}
			if (itemId != null) {
				q += "and ic.item.id = :itemId ";
			}
			if (supplierId != null) {
				q += "and supplier.id = :supplierId ";
			}
			Query query = entityManager.createQuery(q);
			if (nameSearch != null && !nameSearch.isEmpty()) {
				query.setParameter("nameSearch", nameSearch);
			}
			if (supplierId != null) {
				query.setParameter("supplierId", supplierId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			@SuppressWarnings("unchecked")
			List<Component> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
			long total = query.getResultStream().count();
			Page<Component> page = new PageImpl<Component>(result, pageable, total);
			return page;

		}
	}
}