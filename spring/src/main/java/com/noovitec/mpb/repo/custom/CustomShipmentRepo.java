package com.noovitec.mpb.repo.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

public interface CustomShipmentRepo {
	List<Long> findIds(String number, Long customerId, Long saleId);

	@Repository
	public class CustomShipmentRepoImpl implements CustomShipmentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomShipmentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public List<Long> findIds(String number, Long customerId, Long saleId) {
			String q = "select distinct ship.id from Shipment ship " 
					+ "left join ship.shipmentItems shipItem " 
					+ "left join shipItem.saleItem si "
					+ "left join si.sale s " 
					+ "left join ship.customer cu " 
					+ "where ship.id is not null ";
			if (number != null && !number.isEmpty()) {
				q += "and (upper(ship.number) like concat('%',upper(:number),'%')) ";
			}
			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}
			Query query = entityManager.createQuery(q);
			if (number != null && !number.isEmpty()) {
				query.setParameter("number", number);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			List<Long> list = query.getResultList();	
			return list;
		}
	}
}