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
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Component;

public interface CustomComponentRepo {
	Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId, String unitFilter,
			Long componentTypeId);

	@Repository
	public class CustomComponentRepoImpl implements CustomComponentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomComponentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId, String unitFilter,
				Long componentTypeId) {
			String q = "select distinct c from Component c "
					+ "left join c.supplier supplier "
					+ "left join c.itemComponents ic "
					+ "left join c.componentType ct "
					+ "where c.id is not null ";
			if (nameSearch != null && !nameSearch.isEmpty()) {
				q += "and (upper(c.number) like concat('%',upper(:nameSearch),'%') ";
				q += "or upper(c.name) like concat('%',upper(:nameSearch),'%')) ";
			}
			if (itemId != null) {
				q += "and ic.item.id = :itemId ";
			}
			if (componentTypeId != null) {
				q += "and ct.id = :componentTypeId ";
			}
			if (supplierId != null) {
				q += "and supplier.id = :supplierId ";
			}
			if (unitFilter !=null && unitFilter.equalsIgnoreCase("ONLY_SHORT")) {
				q += "and c.unitsShort > 0";
			}
			if (unitFilter !=null && unitFilter.equalsIgnoreCase("ON_STOCK")) {
				q += "and c.unitsOnStock > 0";
			}
			if (unitFilter !=null && unitFilter.equalsIgnoreCase("OPEN_SALE")) {
				q += "and c.unitsSoldNotProd > 0";
			}
			Order order = pageable.getSort().iterator().next();
			q += "order by c."+order.getProperty() + " "+order.getDirection();
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
			if (componentTypeId != null) {
				query.setParameter("componentTypeId", componentTypeId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Component> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<Component> page = new PageImpl<Component>(result, pageable, total);
			return page;
		}
	}
}