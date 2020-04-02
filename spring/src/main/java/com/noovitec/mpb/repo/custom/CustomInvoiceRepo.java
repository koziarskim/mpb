package com.noovitec.mpb.repo.custom;

import java.util.List;

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

import com.noovitec.mpb.entity.Invoice;

public interface CustomInvoiceRepo {
	public Page<Invoice> findPagable(Pageable pageable, String invoiceNumer, Long saleId, Long customerId, Long shipmentId);
	public boolean findBySale(Long saleId);
	
	@Repository
	public class CustomInvoiceRepoImpl implements CustomInvoiceRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Invoice> findPagable(Pageable pageable, String invoiceNumer, Long saleId, Long customerId, Long shipmentId) {
			String q = "select distinct inv from Invoice inv "
					+ "join inv.shipment ship "
					+ "join ship.shipmentItems shipItem "
					+ "join shipItem.saleItem si "
					+ "join si.sale s "
					+ "join ship.customer cu "
					+ "where inv.id is not null ";
			if (invoiceNumer != null && !invoiceNumer.isEmpty()) {
				q += "and (upper(inv.number) like concat('%',upper(:invoiceNumber),'%')) ";
			}
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (shipmentId != null) {
				q += "and ship.id = :shipmentId ";
			}
			Order order = pageable.getSort().iterator().next();
			q += "order by inv."+order.getProperty() + " "+order.getDirection();
			Query query = entityManager.createQuery(q);
			if (invoiceNumer != null && !invoiceNumer.isEmpty()) {
				query.setParameter("invoiceNumer", invoiceNumer);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			if (shipmentId != null) {
				query.setParameter("shipmentId", shipmentId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Invoice> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<Invoice> page = new PageImpl<Invoice>(result, pageable, total);
			return page;
		}
		
		public boolean findBySale(Long saleId) {
			String q = "select inv.id from Invoice inv "
					+ "join inv.invoiceItems ii "
					+ "join ii.saleItem si "
					+ "join si.sale s "
					+ "where s.id = :saleId "
					+ "order by inv.created desc ";
			Query query = entityManager.createQuery(q);
			query.setParameter("saleId", saleId);
			int result = query.getFirstResult();
			if(result>0) {
				return true;
			}
			return false;
		}
	}
}