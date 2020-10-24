package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.Arrays;
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
	public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumer, Long itemId, Long saleId, Long customerId, Long shipmentId,
			LocalDate invoiceFrom, LocalDate invoiceTo, String sent);
	public boolean findBySale(Long saleId);
	public void deleteByShipment(Long shipmentId);
	
	@Repository
	public class CustomInvoiceRepoImpl implements CustomInvoiceRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo, String sent) {
			List<Long> ids = this.getIds(pageable, totals, invoiceNumber, itemId, saleId, customerId, shipmentId, invoiceFrom, invoiceTo, sent);
			Query query = entityManager.createQuery("select count(*) from Invoice inv where inv.id in :ids");
			query.setParameter("ids", ids);
			Page<?> page = null;
			long total = (long) query.getSingleResult();
			if(totals) {
				query = entityManager.createQuery("select distinct sum(inv.totalAmount), sum(inv.payments) from Invoice inv where inv.id in :ids");
				query.setParameter("ids", ids);
				Object result = query.getSingleResult();
				page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			}else {
				String q = "select distinct inv from Invoice inv where inv.id in :ids ";
				Order order = pageable.getSort().iterator().next();
				q += "order by inv."+order.getProperty() + " "+order.getDirection();
				q += ", cast(inv.number as string) asc ";
				query = entityManager.createQuery(q);
				query.setParameter("ids", ids);
				@SuppressWarnings("unchecked")
				List<Invoice> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				page = new PageImpl<Invoice>(result, pageable, total);
				return page;
			}
		}
		
		public List<Long> getIds(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo, String sent) {
			String q = "select distinct inv.id from Invoice inv "
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
			if(sent !=null) {
				q += "and inv.sent = :sent ";
			}
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
			if(sent !=null) {
				query.setParameter("sent", sent.equalsIgnoreCase("YES")?true:false);
			}
			List<Long> ids = query.getResultList();
			return ids;
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