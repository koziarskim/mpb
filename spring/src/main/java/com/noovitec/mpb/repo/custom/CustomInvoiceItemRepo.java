package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.custom.CustomInvoiceRepo.CustomInvoiceRepoImpl;

public interface CustomInvoiceItemRepo {
	public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumer, Long itemId, Long saleId, Long customerId, Long shipmentId,
			LocalDate invoiceFrom, LocalDate invoiceTo, Long brandId);
	
	@Repository
	public class CustomInvoiceItemRepoImpl implements CustomInvoiceItemRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPagable(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo, Long brandId) {
			List<Long> ids = this.getIds(pageable, totals, invoiceNumber, itemId, saleId, customerId, shipmentId, invoiceFrom, invoiceTo, brandId);
			Query query = entityManager.createQuery("select count(*) from InvoiceItem invItem where invItem.id in :ids");
			query.setParameter("ids", ids);
			long total = (long) query.getSingleResult();
			if(totals) {
				query = entityManager.createQuery("select distinct sum(invItem.totalUnitPrice), sum(invItem.unitsInvoiced) from InvoiceItem invItem where invItem.id in :ids");
				query.setParameter("ids", ids);
				Object result = query.getSingleResult();
				Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
				return page;
			}else {
				String q = "select distinct invItem, inv from InvoiceItem invItem "
						+ "join invItem.invoice inv "
						+ "where invItem.id in :ids ";
				Order order = pageable.getSort().iterator().next();
				q += "order by invItem."+order.getProperty() + " "+order.getDirection();
				q += ", cast(inv.number as string) asc ";
				query = entityManager.createQuery(q);
				query.setParameter("ids", ids);
				@SuppressWarnings("unchecked")
				List<Object[]> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
				List<InvoiceItem> entities = new ArrayList<InvoiceItem>();
				result.forEach(o -> entities.add((InvoiceItem) o[0]));
				Page<InvoiceItem> page = new PageImpl<InvoiceItem>(entities, pageable, total);
				return page;
			}
		}
		
		private List<Long> getIds(Pageable pageable, boolean totals, String invoiceNumber, Long itemId, Long saleId, Long customerId, Long shipmentId,
				LocalDate invoiceFrom, LocalDate invoiceTo, Long brandId) {
			String q = "select distinct invItem.id from InvoiceItem invItem "
					+ "join invItem.invoice inv "
					+ "join inv.shipment ship "
					+ "join invItem.saleItem si "
					+ "join si.sale s "
					+ "join si.itemPackaging ip "
					+ "join ip.item i "
					+ "join ship.customer cu "
					+ "join i.brand brand "
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
			if (brandId != null) {
				q += "and brand.id = :brandId ";
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
			if (brandId != null) {
				query.setParameter("brandId", brandId);
			}
			@SuppressWarnings("unchecked")
			List<Long> ids = query.getResultList();
			if(ids.size()==0) {
				ids.add(0L);
			}
			return ids;
		}

	}
}