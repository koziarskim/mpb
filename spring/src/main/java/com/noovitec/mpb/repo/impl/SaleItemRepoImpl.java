package com.noovitec.mpb.repo.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.repo.custom.CustomSaleItemRepo;

@Repository
public class SaleItemRepoImpl implements CustomSaleItemRepo {

	private final Logger log = LoggerFactory.getLogger(SaleItemRepoImpl.class);

    @PersistenceContext
    EntityManager entityManager;

	@Override
	public List<Long> findIds(String numberName, Long customerId, Long itemId) {
		String q = "select distinct si.id from SaleItem si ";
//		q += "where se.id is not null ";
//		if(!searchDto.getSeasonName().isEmpty()) {
//			q += "and upper(se.name) like concat('%',upper(:seasonName),'%')";
//		}
		Query query = entityManager.createQuery(q);
//		if(!searchDto.getCustomers().isEmpty()) {
//			query.setParameter("customerIds", searchDto.getCustomers());
//		}
//		if(!searchDto.getSeasonName().isBlank()) {
//			query.setParameter("seasonName", searchDto.getSeasonName());
//		}
		List<Long> list = query.getResultList();
		return list;
	}
}