package com.noovitec.mpb.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SearchDto;
import com.noovitec.mpb.repo.interfaces.SearchRepoCustom;

@Repository
public class SearchRepoImpl implements SearchRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

	@Override
	public List<KeyValueDto> findSeasons(SearchDto searchDto) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Season s ";
		q += "where s.id is not null ";
		if(searchDto.getSeasonName()!=null) {
			q += "and upper(s.name) like concat('%',upper(:seasonName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(searchDto.getSeasonName()!=null) {
			query.setParameter("seasonName", searchDto.getSeasonName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findCustomers(SearchDto searchDto) {
		if(searchDto.getSeasons().size()==0) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c ";
		q += "join Sale s on s.customer.id = c.id ";
		q += "join SaleItem si on si.sale.id = s.id ";
		q += "join Item i on si.item.id = i.id ";
		q += "join Season season on i.season.id = season.id ";
		q += "where season.id in (:seasonIds) ";
		if(searchDto.getCustomerName()!=null) {
			q += "and upper(c.name) like concat('%',upper(:customerName),'%')";
		}
		Query query = entityManager.createQuery(q);
		query.setParameter("seasonIds", searchDto.getSeasons());
		if(searchDto.getCustomerName()!=null) {
			query.setParameter("customerName", searchDto.getCustomerName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findItems(SearchDto searchDto) {
		if(searchDto.getCustomers().size()==0) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) from Item i ";
		q += "join SaleItem si on si.item.id = i.id ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Customer c on s.customer.id = c.id ";
		q += "where c.id in (:customerIds) ";
		if(searchDto.getItemName()!=null) {
			q += "and upper(i.name) like concat('%',upper(:itemName),'%')";
		}
		Query query = entityManager.createQuery(q);
		query.setParameter("customerIds", searchDto.getCustomers());
		if(searchDto.getItemName()!=null) {
			query.setParameter("itemName", searchDto.getItemName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findSales(SearchDto searchDto) {
		if(searchDto.getItems().size()==0) {
			return new ArrayList<KeyValueDto>();
		}
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(si.id, s.number) from SaleItem si ";
		q += "join Sale s on si.sale.id = s.id ";
		q += "join Item i on si.item.id = i.id ";
		q += "where i.id in (:itemIds) ";
		if(searchDto.getSaleNumber()!=null) {
			q += "and upper(s.number) like concat('%',upper(:saleNumber),'%')";
		}
		Query query = entityManager.createQuery(q);
		query.setParameter("itemIds", searchDto.getItems());
		if(searchDto.getSaleNumber()!=null) {
			query.setParameter("saleNumber", searchDto.getSaleNumber());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findSuppliers(SearchDto searchDto) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Supplier s ";
		q += "where s.id is not null ";
		if(searchDto.getSupplierName()!=null) {
			q += "and upper(s.name) like concat('%',upper(:supplierName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(searchDto.getSupplierName()!=null) {
			query.setParameter("supplierName", searchDto.getSupplierName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findComponents(SearchDto searchDto) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Component c ";
		q += "where c.id is not null ";
		if(searchDto.getComponentName()!=null) {
			q += "and upper(c.name) like concat('%',upper(:componentName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(searchDto.getComponentName()!=null) {
			query.setParameter("componentName", searchDto.getComponentName());
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

}