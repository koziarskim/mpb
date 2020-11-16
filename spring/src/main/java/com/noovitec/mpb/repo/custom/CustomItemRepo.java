package com.noovitec.mpb.repo.custom;

import java.util.ArrayList;
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

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Shipment;

public interface CustomItemRepo {
	public Page<Item> findPagable(Pageable pageable, String numberName, Long componentId, Long brandId, Long categoryId, String unitsFilter);

	@Repository
	public class CustomItemRepoImpl implements CustomItemRepo {

		private final Logger log = LoggerFactory.getLogger(CustomItemRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Item> findPagable(Pageable pageable, String numberName, Long componentId, Long brandId, Long categoryId, String unitsFilter) {
			String q = "select distinct i from Item i "
					+ "left join i.itemComponents ic "
					+ "left join ic.component c "
					+ "left join i.brand b "
					+ "left join i.category cat "
					+ "where i.id is not null ";
			if(numberName!=null && !numberName.isBlank()) {
				q += "and (upper(i.name) like concat('%',upper(:numberName),'%') "
					+ "or upper(i.number) like concat('%',upper(:numberName),'%')) ";
			}
			if(componentId != null) {
				q += "and c.id = :componentId ";
			}
			if(brandId != null) {
				q += "and b.id = :brandId ";
			}
			if(categoryId != null) {
				q += "and cat.id = :categoryId ";
			}
			if(unitsFilter != null) {
				if(unitsFilter.equalsIgnoreCase("ON_FLOOR")) {
					q += "and (i.unitsProduced - i.unitsShipped) != 0 ";
				}
				if(unitsFilter.equalsIgnoreCase("ON_STOCK")) {
					q += "and i.unitsOnStock != 0 ";
				}
				if(unitsFilter.equalsIgnoreCase("NOT_ASSIGNED")) {
					q += "and i.salesNotAssigned != 0 ";
				}
				if(unitsFilter.equalsIgnoreCase("SHORT")) {
					q += "and i.unitsShort != 0 ";
				}
			}
			q += "order by i.salesNotAssigned desc";
			Query query = entityManager.createQuery(q);
			if(numberName!=null && !numberName.isBlank()) {
				query.setParameter("numberName", numberName);
			}
			if(componentId != null) {
				query.setParameter("componentId", componentId);
			}
			if(brandId != null) {
				query.setParameter("brandId", brandId);
			}
			if(categoryId != null) {
				query.setParameter("categoryId", categoryId);
			}
			long total = query.getResultStream().count();
//			@SuppressWarnings("unchecked")
//			List<Object[]> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
//				.setMaxResults(pageable.getPageSize()).getResultList();
//			List<Item> entities = new ArrayList<Item>();
//			result.forEach(o -> entities.add((Item) o[0]));
//			Page<Item> page = new PageImpl<Item>(entities, pageable, total);
			@SuppressWarnings("unchecked")
			List<Item> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
			Page<Item> page = new PageImpl<Item>(result, pageable, total);
			return page;
		}
	}
}