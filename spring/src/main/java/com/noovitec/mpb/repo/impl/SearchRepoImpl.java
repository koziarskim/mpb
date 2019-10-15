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
	public List<KeyValueDto> findFiltered(String itemName, Long supplierId) {
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


}