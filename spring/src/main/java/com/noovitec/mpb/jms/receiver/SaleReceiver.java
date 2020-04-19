package com.noovitec.mpb.jms.receiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.jms.message.JmsSaleMessage;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.service.InvoiceService;
import com.noovitec.mpb.service.NotificationService;

public interface SaleReceiver {
	public void updateHandler(JmsSaleMessage message);
	
	@Transactional
	@Service("saleReceiverImpl")
	public class SaleReceiverImpl implements SaleReceiver{
		
		private final Logger log = LoggerFactory.getLogger(SaleReceiverImpl.class);
		@Autowired
		private NotificationService notificationService;
		@Autowired
		private InvoiceService invoiceService;
		@Autowired
		private SaleRepo saleRepo;
	
		public void updateHandler(JmsSaleMessage message) {
			List<String> emails = null;
			Map<String, String> body = new HashMap<String, String>();
			Sale sale = null;
			//Sale ready/pending approval
			boolean oldPendingApproval = message.isOldPendingApproval();
			boolean pendingApproval = message.isPendingApproval();
			if(!oldPendingApproval && pendingApproval) {
				if(sale == null) {
					sale = saleRepo.getOne(message.getId());
				}
				emails = new ArrayList<>(Arrays.asList("vtomasik@marketplacebrands.com"));
				body.put("saleNumber", sale.getNumber());
				notificationService.sendMail(emails, body, Notification.TYPE.SALE_READY);
			}
			//Sale shipped
			long oldUnitsShipped = message.getOldUnitsShipped();
			long unitsShipped = message.getUnitsShipped();
			long unitsSold = message.getUnitsSold();
			Invoice invoice = null;
			if(unitsShipped > 0 && oldUnitsShipped != unitsShipped && unitsSold > 0) {
				if(sale == null) {
					sale = saleRepo.getOne(message.getId());
				}				
				invoice = invoiceService.createInvoiceForSale(sale);
			}
			if(unitsShipped > 0 && oldUnitsShipped != unitsShipped && unitsSold > 0 &&  unitsShipped >= unitsSold) {
				if(sale == null) {
					sale = saleRepo.getOne(message.getId());
				}
				invoice = invoiceService.createInvoiceForSale(sale);
			}
	        if(invoice!=null) {
				emails = new ArrayList<>(Arrays.asList("kfiolek@marketplacebrands.com"));
				body.put("invoiceNumber", invoice.getNumber());
				notificationService.sendMail(emails, body, Notification.TYPE.INVOICE_CREATED);
	        }
		}
	}
	
	@Component
	public class SaleListener {
		
		@Autowired
		private SaleReceiver saleReceiver;
		
		@JmsListener(destination = "saleUpdated", containerFactory = "myFactory")
		public void updateEvent(JmsSaleMessage message) {
			try {
				MpbTenantContext.setCurrentTenant(message.getTenant());
				saleReceiver.updateHandler(message);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
