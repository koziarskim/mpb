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
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Invoice;

public interface CustomInvoiceRepo {
	public Page<Invoice> findPagable(Pageable pageable, String invoiceNumer, Long itemId, Long saleId, Long customerId, Long shipmentId,
			LocalDate invoiceFrom, LocalDate invoiceTo);
	public boolean findBySale(Long saleId);
	public void deleteByShipment(Long shipmentId);
	
	@Repository
	public class CustomInvoiceRepoImpl implements CustomInvoiceRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Invoice> findPagable(Pageable pageable, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo) {
			String q = "select distinct inv from Invoice inv "
					+ "join inv.shipment ship "
					+ "join inv.invoiceItems invItem "
					+ "join invItem.saleItem si "
					+ "join si.sale s "
					+ "join si.item i "
					+ "join ship.customer cu "
					+ "where inv.id is not null ";
			if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
				q += "and upper(inv.number) = upper(:invoiceNumber) ";
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}			
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}			
			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (shipmentId != null) {
				q += "and ship.id = :shipmentId ";
			}
			if(invoiceFrom != null) {
				q += "and inv.date >= :invoiceFrom ";
			}
			if(invoiceTo !=null) {
				q += "and inv.date <= :invoiceTo ";
			}
			Order order = pageable.getSort().iterator().next();
			q += "order by inv."+order.getProperty() + " "+order.getDirection();
			Query query = entityManager.createQuery(q);
			if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
				query.setParameter("invoiceNumber", invoiceNumber);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			if (shipmentId != null) {
				query.setParameter("shipmentId", shipmentId);
			}
			if(invoiceFrom !=null) {
				query.setParameter("invoiceFrom", invoiceFrom);
			}
			if(invoiceTo !=null) {
				query.setParameter("invoiceTo", invoiceTo);
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
		
		public void deleteByShipment(Long shipmentId) {
			String q = "select inv from Invoice inv "
					+ "join inv.shipment ship "
					+ "where ship.id = :shipmentId";
			Query query = entityManager.createQuery(q);
			query.setParameter("shipmentId", shipmentId);
			List<Invoice> invoices = query.getResultList();
			for(Invoice invoice: invoices) {
				String q2 = "delete from InvoiceItem ii where ii.invoice.id = :invoiceId";
				Query query2 = entityManager.createQuery(q2);
				query2.setParameter("invoiceId", invoice.getId());
				query2.executeUpdate();
				String q3 = "delete from Invoice inv where inv.id = :invoiceId";
				Query query3 = entityManager.createQuery(q3);
				query3.setParameter("invoiceId", invoice.getId());
				query3.executeUpdate();
				
			}
		}
	}
}