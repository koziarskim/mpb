package com.noovitec.mpb.repo.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.repo.interfaces.SearchRepoCustom;

@Repository
public class SearchRepoImpl implements SearchRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

	@Override
	public List<KeyValueDto> findSeasons(String seasonName) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Season s ";
		q += "where s.id is not null ";
		if(seasonName!=null) {
			q += "and upper(s.name) like concat('%',upper(:seasonName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(seasonName!=null) {
			query.setParameter("seasonName", seasonName);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findCustomers(String customerName) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c ";
		q += "where c.id is not null ";
		if(customerName!=null) {
			q += "and upper(c.name) like concat('%',upper(:customerName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(customerName!=null) {
			query.setParameter("customerName", customerName);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findItems(String itemName, Long supplierId) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) from Item i ";
		if(supplierId!=null) {
			q += "join ItemComponent ic on ic.item.id = i.id ";
			q += "join Component c on ic.component.id = c.id ";
		}
		q += "where i.id is not null ";
		if(itemName!=null) {
			q += "and upper(i.name) like concat('%',upper(:itemName),'%')";
		}
		if(supplierId!=null) {
			q += "and c.supplier.id = :supplierId";
		}
		Query query = entityManager.createQuery(q);
		if(itemName!=null) {
			query.setParameter("itemName", itemName);
		}
		if(supplierId!=null) {
			query.setParameter("supplierId", supplierId);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findComponents(String componentName) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Component c ";
		q += "where c.id is not null ";
		if(componentName!=null) {
			q += "and upper(c.name) like concat('%',upper(:componentName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(componentName!=null) {
			query.setParameter("componentName", componentName);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findSuppliers(String supplierName) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Supplier s ";
		q += "where s.id is not null ";
		if(supplierName!=null) {
			q += "and upper(s.name) like concat('%',upper(:supplierName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(supplierName!=null) {
			query.setParameter("supplierName", supplierName);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

	@Override
	public List<KeyValueDto> findSales(String saleNumber) {
		String q = "select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.number) from Sale s ";
		q += "where s.id is not null ";
		if(saleNumber!=null) {
			q += "and upper(s.number) like concat('%',upper(:saleNumber),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(saleNumber!=null) {
			query.setParameter("saleNumber", saleNumber);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}

}