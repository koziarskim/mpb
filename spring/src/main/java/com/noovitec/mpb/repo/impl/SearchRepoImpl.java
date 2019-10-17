package com.noovitec.mpb.repo.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.PoComponentDto;
import com.noovitec.mpb.dto.SearchDto;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.interfaces.SearchRepoCustom;

@Repository
public class SearchRepoImpl implements SearchRepoCustom {

	private final Logger log = LoggerFactory.getLogger(SearchRepoImpl.class);

    @PersistenceContext
    EntityManager entityManager;

	@Override
	public List<KeyValueDto> findSeasons(SearchDto searchDto) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Season s ";
		q += "where s.id is not null ";
		if(!searchDto.getSeasonName().isEmpty()) {
			q += "and upper(s.name) like concat('%',upper(:seasonName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasonName().isBlank()) {
			query.setParameter("seasonName", searchDto.getSeasonName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findCustomers(SearchDto searchDto) {
		if(searchDto.getSeasons().isEmpty() && searchDto.getCustomerName().isBlank()) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c ";
		q += "join Sale s on s.customer.id = c.id ";
		q += "join SaleItem si on si.sale.id = s.id ";
		q += "join Item i on si.item.id = i.id ";
		q += "join Season season on i.season.id = season.id ";
		q += "where c.id is not null ";
		if(!searchDto.getSeasons().isEmpty()) {
			q += "and season.id in (:seasonIds) ";
		}
		if(!searchDto.getCustomerName().isBlank()) {
			q += "and upper(c.name) like concat('%',upper(:customerName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasons().isEmpty()) {
			query.setParameter("seasonIds", searchDto.getSeasons());
		}
		if(!searchDto.getCustomerName().isBlank()) {
			query.setParameter("customerName", searchDto.getCustomerName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findItems(SearchDto searchDto) {
		if(searchDto.getCustomers().isEmpty() && searchDto.getItemName().isBlank()) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) from Item i ";
		q += "join SaleItem si on si.item.id = i.id ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Item i on si.item.id = i.id ";
		q += "join Season se on i.season.id = se.id ";
		q += "join Customer c on s.customer.id = c.id ";
		q += "where i.id is not null ";
		if(!searchDto.getSeasons().isEmpty()) {
			q += "and se.id in (:seasonIds) ";
		}
		if(!searchDto.getCustomers().isEmpty()) {
			q += "and c.id in (:customerIds) ";
		}
		if(!searchDto.getItemName().isBlank()) {
			q += "and upper(i.name) like concat('%',upper(:itemName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasons().isEmpty()) {
			query.setParameter("seasonIds", searchDto.getSeasons());
		}
		if(!searchDto.getCustomers().isEmpty()) {
			query.setParameter("customerIds", searchDto.getCustomers());
		}
		if(!searchDto.getItemName().isBlank()) {
			query.setParameter("itemName", searchDto.getItemName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<SaleItem> findSales(SearchDto searchDto) {
		if(searchDto.getSeasons().isEmpty() && searchDto.getItems().isEmpty()
				&& searchDto.getCustomers().isEmpty() && searchDto.getSaleNumber().isBlank()) {
			return new ArrayList<SaleItem>();
		}
		String q = "select distinct si.id from SaleItem si ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Item i on si.item.id = i.id ";
		q += "join Season se on i.season.id = se.id ";
		q += "join Customer cu on s.customer.id = cu.id ";
		q += "where si.id is not null ";
		if(!searchDto.getSeasons().isEmpty()) {
			q += "and se.id in (:seasonIds) ";
		}
		if(!searchDto.getCustomers().isEmpty()) {
			q += "and cu.id in (:customerIds) ";
		}
		if(!searchDto.getItems().isEmpty()) {
			q += "and i.id in (:itemIds) ";
		}
		if(!searchDto.getSaleNumber().isEmpty()) {
			q += "and upper(s.number) like concat('%',upper(:saleNumber),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasons().isEmpty()) {
			query.setParameter("seasonIds", searchDto.getSeasons());
		}
		if(!searchDto.getCustomers().isEmpty()) {
			query.setParameter("customerIds", searchDto.getCustomers());
		}
		if(!searchDto.getItems().isEmpty()) {
			query.setParameter("itemIds", searchDto.getItems());
		}
		if(!searchDto.getSaleNumber().isBlank()) {
			query.setParameter("saleNumber", searchDto.getSaleNumber());
		}
		@SuppressWarnings("unchecked")
		List<Long> ids = query.getResultList();
		if(ids.isEmpty()) {
			return new ArrayList<SaleItem>();
		}
		@SuppressWarnings("unchecked")
		List<SaleItem> sales = entityManager.createQuery("select si from SaleItem si where si.id in (:ids) ")
				.setParameter("ids", ids).getResultList();
		return sales;
	}

	@Override
	public List<KeyValueDto> findSuppliers(SearchDto searchDto) {
		if(searchDto.getSales().isEmpty() && searchDto.getItems().isEmpty() 
				&& searchDto.getCustomers().isEmpty() && searchDto.getSupplierName().isBlank()) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Supplier s ";
		q += "join Component c on c.supplier.id = s.id ";
		q += "join ItemComponent ic on ic.component.id = c.id ";
		q += "join Item i on ic.item.id = i.id ";
		q += "join Season se on i.season.id = se.id ";
		q += "join SaleItem si on si.item.id = i.id ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Customer cu on s.customer.id = cu.id ";
		q += "where s.id is not null ";
		if(!searchDto.getSeasons().isEmpty()) {
			q += "and se.id in (:seasonIds) ";
		}
		if(!searchDto.getCustomers().isEmpty()){
			q += "and cu.id in (:customerIds) ";
		}
		if(!searchDto.getItems().isEmpty()){
			q += "and i.id in (:itemIds) ";
		}
		if(!searchDto.getSales().isEmpty()){
			q += "and si.id in (:saleIds) ";
		}
		if(!searchDto.getSupplierName().isBlank()) {
			q += "and upper(s.name) like concat('%',upper(:supplierName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasons().isEmpty()){
			query.setParameter("seasonIds", searchDto.getSeasons());
		}
		if(!searchDto.getCustomers().isEmpty()){
			query.setParameter("customerIds", searchDto.getCustomers());
		}
		if(!searchDto.getItems().isEmpty()){
			query.setParameter("itemIds", searchDto.getItems());
		}
		if(!searchDto.getSales().isEmpty()){
			query.setParameter("saleIds", searchDto.getSales());
		}
		if(!searchDto.getSupplierName().isBlank()) {
			query.setParameter("supplierName", searchDto.getSupplierName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<Component> findComponents(SearchDto searchDto) {
		if(searchDto.getSuppliers().isEmpty() && searchDto.getSales().isEmpty() && searchDto.getItems().isEmpty() && searchDto.getComponentName().isBlank()) {
			return new ArrayList<Component>();
		}
		String q = "select distinct c.id from Component c ";
		q += "join ItemComponent ic on ic.component.id = c.id ";
		q += "join Item i on ic.item.id = i.id ";
		q += "join SaleItem si on si.item.id = i.id ";
		q += "join Season se on i.season.id = se.id ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Customer cu on s.customer.id = cu.id ";
		q += "where c.id is not null ";
		if(!searchDto.getSeasons().isEmpty()) {
			q += "and se.id in (:seasonIds) ";
		}
		if(!searchDto.getCustomers().isEmpty()){
			q += "and cu.id in (:customerIds) ";
		}
		if(!searchDto.getItems().isEmpty()) {
			q += "and i.id in (:itemIds) ";
		}
		if(!searchDto.getSales().isEmpty()) {
			q += "and si.id in (:saleIds) ";
		}
		if(!searchDto.getSuppliers().isEmpty()) {
			q += "and c.supplier.id in (:supplierIds) ";
		}
		if(!searchDto.getComponentName().isBlank()) {
			q += "and upper(c.name) like concat('%',upper(:componentName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(!searchDto.getSeasons().isEmpty()){
			query.setParameter("seasonIds", searchDto.getSeasons());
		}
		if(!searchDto.getCustomers().isEmpty()){
			query.setParameter("customerIds", searchDto.getCustomers());
		}
		if(!searchDto.getItems().isEmpty()) {
			query.setParameter("itemIds", searchDto.getItems());
		}
		if(!searchDto.getSales().isEmpty()) {
			query.setParameter("saleIds", searchDto.getSales());
		}
		if(!searchDto.getSuppliers().isEmpty()) {
			query.setParameter("supplierIds", searchDto.getSuppliers());
		}
		if(!searchDto.getComponentName().isBlank()) {
			query.setParameter("componentName", searchDto.getComponentName());
		}
		@SuppressWarnings("unchecked")
		List<Long> ids = query.getResultList();
		if(ids.isEmpty()) {
			return new ArrayList<Component>();
		}
		@SuppressWarnings("unchecked")
		List<Component> components = entityManager.createQuery("select c from Component c where c.id in (:ids) ")
				.setParameter("ids", ids).getResultList();
		return components;
	}

	@Override
	public List<PoComponentDto> findPoComponents(SearchDto searchDto) {
		if(searchDto.getComponents().size()==0) {
			return new ArrayList<PoComponentDto>();
		}
		@SuppressWarnings("unchecked")
		List<Component> components = entityManager.createQuery("select c from Component c where c.id in (:ids) ")
				.setParameter("ids", searchDto.getComponents()).getResultList();
		List<PoComponentDto> dtos = new ArrayList<PoComponentDto>();
		for(Component c: components) {
			PoComponentDto dto = new PoComponentDto();
			dto.setId(c.getId());
			dto.setName(c.getName());
			dto.setUnitsOnStock(Long.valueOf(c.getUnitsOnStack()));
			dto.setUnitsInOrder(c.getUnitsInOrder());
			dto.setUnitPrice(c.getUnitCost());
			dto.setUnitCost(c.getUnitCost());
			Long unitsSold = 0L;
			Long unitsProduced = 0L;
			Long totalSold = 0L;
			Long totalProduced = 0L;
			for(ItemComponent ic: c.getItemComponents()) {
				Long unitsInItem = ic.getUnits().longValue();
				for(SaleItem si: ic.getItem().getSaleItems()) {
					if(searchDto.getItems().contains(ic.getItem().getId()) && searchDto.getSales().contains(si.getId())) {
						unitsSold += (unitsInItem * si.getUnits());
						unitsProduced += (unitsInItem * si.getUnitsProduced());
					}
					totalSold += (unitsInItem * si.getUnits());
					totalProduced += (unitsInItem * si.getUnitsProduced());
				}
			}
			dto.setUnitsSold(unitsSold);
			dto.setUnitsProduced(unitsProduced);
			dto.setTotalSold(totalSold);
			dto.setTotalProduced(totalProduced);
			dto.setUnits(dto.getUnitsSold() - dto.getUnitsProduced() - dto.getUnitsInOrder() - dto.getUnitsOnStock());
			BigDecimal totalPrice = (dto.getUnitPrice()==null || dto.getUnits()==null)?BigDecimal.ZERO:dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getUnits(), 2));
			dto.setTotalPrice(totalPrice);
			dtos.add(dto);
		}
		return dtos;
	}	
}