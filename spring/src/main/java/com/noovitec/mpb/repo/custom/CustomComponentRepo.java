package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
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

import com.noovitec.mpb.dto.ComponentInventoryListDto;
import com.noovitec.mpb.entity.Component;

public interface CustomComponentRepo {
	Page<ComponentInventoryListDto> findInventoryPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId,
			Long categoryId, Long componentTypeId, LocalDate dateTo);
	Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId, String unitFilter,
			Long categoryId, Long componentTypeId);

	@Repository
	public class CustomComponentRepoImpl implements CustomComponentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomComponentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<ComponentInventoryListDto> findInventoryPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId,
				Long categoryId, Long componentTypeId, LocalDate dateTo) {
			String q = "select distinct new com.noovitec.mpb.dto.ComponentInventoryListDto(c.id as id, c.number as number, "
					+ "c.name as name, cat.name, ct.name, supplier.name, 0L, "
					+ "(select ceil(sum(si2.unitsShipped)*max(ic2.units)) from Component c2 "
						+ "join c2.itemComponents ic2 "
						+ "join ic2.item i2 "
						+ "join i2.itemPackagings ip2 "
						+ "join ip2.saleItems si2 "
						+ "join si2.shipmentItems shipItem2 "
						+ "join shipItem2.shipment ship2 "
						+ "where c.id = c2.id "
						+ "and ship2.shippedDate <= :dateTo), "
					+ "(select sum(r3.units) from Component c3 "
						+ "join c3.purchaseComponents pc3 "
						+ "join pc3.receivings r3 "
						+ "where c.id = c3.id "
						+ "and r3.receivingDate <= :dateTo), 0L, c.averagePrice, 0L) "
					+ "from Component c "
					+ "left join c.supplier supplier "
					+ "left join c.itemComponents ic "
					+ "left join c.category cat "
					+ "left join c.componentType ct "
					+ "where c.id is not null ";
			if (nameSearch != null && !nameSearch.isEmpty()) {
				q += "and (upper(c.number) like concat('%',upper(:nameSearch),'%') ";
				q += "or upper(c.name) like concat('%',upper(:nameSearch),'%')) ";
			}
			if (itemId != null) {
				q += "and ic.item.id = :itemId ";
			}
			if (categoryId != null) {
				q += "and cat.id = :categoryId ";
			}
			if (componentTypeId != null) {
				q += "and ct.id = :componentTypeId ";
			}
			if (supplierId != null) {
				q += "and supplier.id = :supplierId ";
			}
			q += "order by c.number asc ";
			Query query = entityManager.createQuery(q);
			query.setParameter("dateTo", dateTo);
			if (nameSearch != null && !nameSearch.isEmpty()) {
				query.setParameter("nameSearch", nameSearch);
			}
			if (supplierId != null) {
				query.setParameter("supplierId", supplierId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (categoryId != null) {
				query.setParameter("categoryId", categoryId);
			}
			if (componentTypeId != null) {
				query.setParameter("componentTypeId", componentTypeId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<ComponentInventoryListDto> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<ComponentInventoryListDto> page = new PageImpl<ComponentInventoryListDto>(result, pageable, total);
			return page;
		}

		@Override
		public Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId, String unitFilter,
				Long categoryId, Long componentTypeId) {
			String q = "select distinct c from Component c "
					+ "left join c.supplier supplier "
					+ "left join c.itemComponents ic "
					+ "left join c.category cat "
					+ "left join c.componentType ct "
					+ "where c.id is not null ";
			if (nameSearch != null && !nameSearch.isEmpty()) {
				q += "and (upper(c.number) like concat('%',upper(:nameSearch),'%') ";
				q += "or upper(c.name) like concat('%',upper(:nameSearch),'%')) ";
			}
			if (itemId != null) {
				q += "and ic.item.id = :itemId ";
			}
			if (categoryId != null) {
				q += "and cat.id = :categoryId ";
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
			if (categoryId != null) {
				query.setParameter("categoryId", categoryId);
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