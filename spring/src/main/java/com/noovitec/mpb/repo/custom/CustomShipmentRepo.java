package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Shipment;

public interface CustomShipmentRepo {
	Page<Shipment> findIds(Pageable pageable, String number, Long customerId, Long saleId, Long itemId, String status, LocalDate shipFrom, LocalDate shipTo);

	@Repository
	public class CustomShipmentRepoImpl implements CustomShipmentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomShipmentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Shipment> findIds(Pageable pageable, String number, Long customerId, Long saleId, Long itemId, String status, LocalDate shipFrom, LocalDate shipTo) {
			String q = "select distinct ship from Shipment ship " 
					+ "left join ship.shipmentItems shipItem " 
					+ "left join shipItem.saleItem si "
					+ "left join si.sale s "
					+ "left join si.item i " 
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
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			if (status != null) {
				q += "and ship.status = :status ";
			}
			if(shipFrom != null) {
				q += "and ship.shippedDate >= :shipFrom ";
			}
			if(shipTo !=null) {
				q += "and ship.shippedDate <= :shipTo ";
			}
			q += "order by ship.status, ship.modifiedDate desc";
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
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (status != null) {
				query.setParameter("status", status);
			}
			if(shipFrom !=null) {
				query.setParameter("shipFrom", shipFrom);
			}
			if(shipTo !=null) {
				query.setParameter("shipTo", shipTo);
			}
			@SuppressWarnings("unchecked")
			List<Shipment> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
			long total = query.getResultStream().count();
			Page<Shipment> page = new PageImpl<Shipment>(result, pageable, total);
			return page;

		}
	}
}