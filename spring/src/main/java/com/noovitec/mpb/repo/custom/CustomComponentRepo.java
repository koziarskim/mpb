package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
			String q = "select distinct c.id as id, c.number as number, c.name as name, '' as a, '' as b, '' as c, null as d, sold.units_shipped, rec.units_received, "
					+ "(sold.units_shipped-rec.units_received) as units_on_floor, "
					+ "c.average_price as unit_price, c.average_price*(sold.units_shipped-rec.units_received) as total " 
					+ "from Component c "
					+ "left join (select ic.component_id as cid, ceil(sum(si.units_shipped)*max(ic.units)) as units_shipped " 
						+ "from item_component ic " 
						+ "join item i on i.id = ic.item_id " 
						+ "join item_packaging ip on ip.item_id = i.id "
						+ "join sale_item si on si.item_packaging_id = ip.id " 
						+ "join shipment_item ship_item on ship_item.sale_item_id = si.id " 
						+ "join shipment ship on ship.id = ship_item.shipment_id "
						+ "where ship.shipped_date <= :dateTo "
						+ "group by ic.component_id) sold on sold.cid = c.id " 
					+ "left join (select pc.component_id as cid, sum(r.units) as units_received " 
						+ "from purchase_component pc "
						+ "join receiving r on r.purchase_component_id = pc.id " 
						+ "where r.receiving_date <= :dateTo "
						+ "group by pc.component_id) rec on rec.cid = c.id " 
					+ "where c.id is not null "
					+ "and (sold.units_shipped-rec.units_received) is not null " 
					+ "and (sold.units_shipped-rec.units_received) <> 0 "
					+ "order by units_on_floor desc ";
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
//			q += "order by c.number asc ";
			Query query = entityManager.createNativeQuery(q);
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
			List<Object[]> res = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			List<ComponentInventoryListDto> result = res.stream().map(ComponentInventoryListDto::new).collect(Collectors.toList());
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