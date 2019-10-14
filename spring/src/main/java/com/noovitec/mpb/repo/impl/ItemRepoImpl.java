package com.noovitec.mpb.repo.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.repo.interfaces.ItemRepoCustom;

@Repository
public class ItemRepoImpl implements ItemRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
    
	@Override
	public List<KeyValueDto> findFiltered(String itemName) {
		String q = "select new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) from Item i ";
		if(itemName!=null) {
			q += "where upper(i.name) like concat('%',upper(:itemName),'%')";
		}
		Query query = entityManager.createQuery(q);
		if(itemName!=null) {
			query.setParameter("itemName", itemName);
		}
		List<KeyValueDto> list = query.getResultList();
		return list;
	}


}