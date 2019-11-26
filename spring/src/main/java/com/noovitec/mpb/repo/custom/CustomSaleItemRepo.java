package com.noovitec.mpb.repo.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

public interface CustomSaleItemRepo {

	List<Long> findIds(String numberName, Long customerId, Long itemId);
	
	@Repository
	public class SaleItemRepoImpl implements CustomSaleItemRepo {

		private final Logger log = LoggerFactory.getLogger(SaleItemRepoImpl.class);

	    @PersistenceContext
	    EntityManager entityManager;

		@Override
		public List<Long> findIds(String numberName, Long customerId, Long itemId) {
			String q = "select distinct si.id from SaleItem si "
					+ "join si.item i "
					+ "join si.sale s "
					+ "join s.customer cu "
					+ "where si.id is not null ";
			if(numberName != null && !numberName.isEmpty()) {
				q += "and (upper(s.number) like concat('%',upper(:numberName),'%') ";
				q += "or upper(s.name) like concat('%',upper(:numberName),'%')) ";
			}
			if(customerId != null) {
				q += "and cu.id = :customerId ";
			}		
			if(itemId != null) {
				q += "and i.id = :itemId ";
			}
			Query query = entityManager.createQuery(q);
			if(numberName != null && !numberName.isEmpty()) {
				query.setParameter("numberName", numberName);
			}
			if(customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if(itemId != null) {
				query.setParameter("itemId", itemId);
			}
			List<Long> list = query.getResultList();
			return list;
		}
	}
}