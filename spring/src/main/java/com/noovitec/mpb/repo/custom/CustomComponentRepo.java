package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.Arrays;
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
	Page<?> findInventoryPage(Pageable pageable, boolean totals, String nameSearch, Long supplierId, 
			Long itemId, Long categoryId, Long componentTypeId, LocalDate dateFrom, LocalDate dateTo,
			boolean positiveFloor, boolean zeroFloor, boolean negativeFloor);
	Page<Component> findPage(Pageable pageable, String nameSearch, Long supplierId, Long itemId, String unitFilter,
			Long categoryId, Long componentTypeId);

	@Repository
	public class CustomComponentRepoImpl implements CustomComponentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomComponentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findInventoryPage(Pageable pageable, boolean totals, String nameSearch, Long supplierId,
				Long itemId, Long categoryId, Long componentTypeId, LocalDate dateFrom, LocalDate dateTo,
				boolean positiveFloor, boolean zeroFloor, boolean negativeFloor) {
			String q = "";
			if(totals) {
				q += "select sum(coalesce(rec.units_received,0)) as units_received, "
						+ "sum(coalesce(prod.units_produced,0)) as units_produced, "
						+ "sum(coalesce(sold.units_shipped,0)) as units_shipped, "
						+ "sum(coalesce(rec.units_received,0)-coalesce(adjusted.units_adjusted,0)-coalesce(prod.units_produced,0)) as comp_on_floor, "
						+ "sum(coalesce(prod.units_produced,0)-coalesce(sold.units_shipped,0)) as prod_on_floor, "
						+ "sum(coalesce(rec.units_received,0)-coalesce(adjusted.units_adjusted,0)-coalesce(sold.units_shipped,0)) as units_on_floor, "
						+ "sum(c.average_price*(coalesce(rec.units_received,0)-coalesce(adjusted.units_adjusted,0)-coalesce(sold.units_shipped,0))) as total,"
						+ "sum(coalesce(adjusted.units_adjusted,0)) as units_adjusted ";
			} else {
				q += "select distinct c.id as id, c.number as number, c.name as name, other.cat_name as categoryName,"
						+ "other.ct_name as componentTypeName, other.su_name as supplierName, other.su_id as supplierId, "
						+ "coalesce(rec.units_received,0) as units_received, "
						+ "coalesce(prod.units_produced,0) as units_produced, "
						+ "coalesce(sold.units_shipped,0) as units_shipped, "
						+ "(coalesce(rec.units_received,0)-coalesce(adjusted.units_adjusted,0)-coalesce(prod.units_produced,0)) as comp_on_floor, "
						+ "(coalesce(prod.units_produced,0)-coalesce(sold.units_shipped,0)) as prod_on_floor, "
						+ "(coalesce(rec.units_received,0)-coalesce(adjusted.units_adjusted,0)-coalesce(sold.units_shipped,0)) as units_on_floor, "
						+ "c.average_price as unit_price, "
						+ "c.average_price*(coalesce(rec.units_received,0)-coalesce(sold.units_shipped,0)) as total, "
						+ "coalesce(adjusted.units_adjusted,0) as units_adjusted ";
			}
				q += "from Component c "
					+ "left join (select distinct ic.component_id as cid, ceil(sum(si.units_shipped*ic.units)) as units_shipped " 
						+ "from item_component ic " 
						+ "left join item i on i.id = ic.item_id " 
						+ "left join item_packaging ip on ip.item_id = i.id "
						+ "left join sale_item si on si.item_packaging_id = ip.id " 
						+ "left join shipment_item ship_item on ship_item.sale_item_id = si.id " 
						+ "left join shipment ship on ship.id = ship_item.shipment_id "
						+ "where ship.shipped_date >= :dateFrom "
						+ "and ship.shipped_date <= :dateTo "
						+ "group by ic.component_id) sold on sold.cid = c.id " 
					+ "left join (select distinct pc.component_id as cid, sum(r.units) as units_received " 
						+ "from purchase_component pc "
						+ "left join receiving r on r.purchase_component_id = pc.id " 
						+ "where r.receiving_date >= :dateFrom "
						+ "and r.receiving_date <= :dateTo "
						+ "group by pc.component_id) rec on rec.cid = c.id "
					+ "left join (select distinct ic.component_id as cid, ceil(sum(p.units_produced*ic.units)) as units_produced " 
						+ "from item_component ic " 
						+ "left join item i on i.id = ic.item_id " 
						+ "left join item_packaging ip on ip.item_id = i.id "
						+ "left join schedule_event se on se.item_packaging_id = ip.id "
						+ "left join production p on p.schedule_event_id = se.id "
						+ "where p.updated >= :dateFrom "
						+ "and p.updated <= :dateTo "
						+ "group by ic.component_id) prod on prod.cid = c.id "
					+ "left join (select c.id as cid, max(cat.id) as cat_id, max(cat.name) as cat_name, max(ct.id) as ct_id, max(ct.name) as ct_name,"
						+ "max(su.id) as su_id, max(su.name) as su_name, max(i.id) as i_id "
						+ "from component c "
						+ "left join item_component ic on ic.component_id = c.id "
						+ "left join item i on i.id = ic.item_id "
						+ "left join supplier su on su.id = c.supplier_id "
						+ "left join shared.category cat on cat.id = c.category_id "
						+ "left join shared.component_type ct on ct.id = c.component_type_id "
						+ "where c.id is not null "
						+ "group by c.id) other on other.cid = c.id "
					+ "left join (select ca.component_id as cid, sum(ca.units_adjusted) as units_adjusted "
						+ "from component_adjustment ca "
						+ "where ca.date >= :dateFrom "
						+ "and ca.date <= :dateTo "
						+ "group by ca.component_id) adjusted on adjusted.cid = c.id "
					+ "where c.id is not null "
					+ "and other.cat_name != 'Non Inventory' "
					+ "and ((coalesce(rec.units_received,0)-coalesce(sold.units_shipped,0)) = 0.1 ";
			if(positiveFloor) {
				q += "or (coalesce(rec.units_received,0)-coalesce(sold.units_shipped,0)) > 0 ";
			}
			if(zeroFloor) {
				q += "or (coalesce(rec.units_received,0)-coalesce(sold.units_shipped,0)) = 0 ";
			}
			if(negativeFloor) {
				q += "or (coalesce(rec.units_received,0)-coalesce(sold.units_shipped,0)) < 0 ";
			}
				q += ")";
			if (nameSearch != null && !nameSearch.isEmpty()) {
				q += "and (upper(c.number) like concat('%',upper(:nameSearch),'%') ";
				q += "or upper(c.name) like concat('%',upper(:nameSearch),'%')) ";
			}
			if (itemId != null) {
				q += "and other.i_id = :itemId ";
			}
			if (categoryId != null) {
				q += "and other.cat_id = :categoryId ";
			}
			if (componentTypeId != null) {
				q += "and other.ct_id = :componentTypeId ";
			}
			if (supplierId != null) {
				q += "and other.su_id = :supplierId ";
			}
			q += "order by units_on_floor desc ";
			Query query = entityManager.createNativeQuery(q);
			query.setParameter("dateFrom", dateFrom);
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
			if(totals) {
				Object result = query.getSingleResult();
				Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			} else {
				@SuppressWarnings("unchecked")
				List<Object[]> res = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				List<ComponentInventoryListDto> result = res.stream().map(ComponentInventoryListDto::new).collect(Collectors.toList());
				Page<ComponentInventoryListDto> page = new PageImpl<ComponentInventoryListDto>(result, pageable, total);
				return page;
			}
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