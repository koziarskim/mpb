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
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.custom.CustomInvoiceRepo.CustomInvoiceRepoImpl;

public interface CustomInvoiceItemRepo {
	public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumer, Long itemId, Long saleId, Long customerId, Long shipmentId,
			LocalDate invoiceFrom, LocalDate invoiceTo);
	
	@Repository
	public class CustomInvoiceItemRepoImpl implements CustomInvoiceItemRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo) {
			Query query = this.getQuery(pageable, totals, invoiceNumber, itemId, saleId, customerId, shipmentId, invoiceFrom, invoiceTo);
			long total = query.getResultStream().count();
			if(totals) {
				@SuppressWarnings("unchecked")
				List<Object> result = query.getResultList();
				Page<Object> page = new PageImpl<Object>(result, pageable, total);
				return page;
			}else {
				@SuppressWarnings("unchecked")
				List<InvoiceItem> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				Page<InvoiceItem> page = new PageImpl<InvoiceItem>(result, pageable, total);
				return page;
			}
		}
		
		private Query getQuery(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo) {
			String q = "";
			if(totals) {
				q += "select distinct sum(invItem.totalUnitPrice), sum(invItem.unitsInvoiced), invItem.id ";
			}else {
				q += "select distinct invItem ";
			}
			q += "from InvoiceItem invItem "
					+ "join invItem.invoice inv "
					+ "join inv.shipment ship "
					+ "join ship.shipmentItems shipItem "
					+ "join shipItem.saleItem si "
					+ "join si.sale s "
					+ "join si.item i "
					+ "join ship.customer cu "
					+ "where invItem.id is not null ";
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
			if(!totals) {
				Order order = pageable.getSort().iterator().next();
				q += "order by invItem."+order.getProperty() + " "+order.getDirection();
			} else {
				q += "group by invItem.id ";
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
			return query;
		}

	}
}