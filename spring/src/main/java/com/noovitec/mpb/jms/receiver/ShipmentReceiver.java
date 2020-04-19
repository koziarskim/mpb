package com.noovitec.mpb.jms.receiver;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.jms.message.JmsEntityMessage;
import com.noovitec.mpb.repo.ShipmentRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.InvoiceService;
import com.noovitec.mpb.service.NotificationService;

public interface ShipmentReceiver {
	public void shipmentUpdatedEvent(JmsEntityMessage message);
	
	@Transactional
	@Service("shipmentReceiverImpl")
	public class ShipmentReceiverImpl implements ShipmentReceiver{
		
		private final Logger log = LoggerFactory.getLogger(ShipmentReceiverImpl.class);
		@Autowired
		NotificationService notificationService;
		@Autowired
		InvoiceService invoiceService;
		@Autowired
		CrudService crudService;
		@Autowired
		ShipmentRepo shipmentRepo;
	
		public void shipmentUpdatedEvent(JmsEntityMessage message) {
			List<String> emails = null;
			Map<String, String> body = new HashMap<String, String>();
			Shipment shipment = null;
			Customer customer = null;
			if(message.getState() != null) {
				Object obj = message.getState()[ArrayUtils.indexOf(message.getPropertyNames(), "customer")];
				customer = obj==null?null:((Customer) obj);
			}
			log.info("Customer: "+customer.getName());
			//Shipment is ready
			boolean ready = false;
			boolean prevReady = false;
			if(message.getState() != null) {
				Object obj = message.getState()[ArrayUtils.indexOf(message.getPropertyNames(), "ready")];
				ready = obj==null?false:((boolean) obj);
			}
			if(message.getOldState() != null) {
				Object obj = message.getOldState()[ArrayUtils.indexOf(message.getPropertyNames(), "ready")];
				prevReady = obj==null?false:((boolean) obj);
			}
			if(!prevReady && ready) {
				shipment = shipmentRepo.findById(message.getId()).get();
				emails = Arrays.asList("shipping@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
				body.put("shipmentNumber", shipment.getNumber());
				notificationService.sendMail(emails, body, Notification.TYPE.SHIPPING_READY);
			}
			//Shipment is shipped;
			LocalDate shippedDate = null;
			LocalDate prevShippedDate = null;
			if(message.getState() != null) {
				Object obj = message.getState()[ArrayUtils.indexOf(message.getPropertyNames(), "shippedDate")];
				if(obj!=null) {
					shippedDate = obj==null?null:((LocalDate) obj);
				}
			}
			if(message.getOldState() != null) {
				Object obj = message.getOldState()[ArrayUtils.indexOf(message.getPropertyNames(), "shippedDate")];
				if(obj!=null) {
					prevShippedDate = obj==null?null:((LocalDate) obj);
				}
			}
			if(prevShippedDate == null && shippedDate !=null) {
				shipment = shipmentRepo.findById(message.getId()).get();
				emails = Arrays.asList("kzygulska@marketplacebrands.com", "kfiolek@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
				body.put("shipmentNumber", shipment.getNumber());
	        	notificationService.sendMail(emails, body, Notification.TYPE.SHIPPING_SHIPPED);
				List<Invoice> invoices = invoiceService.createInvoiceForShipment(shipment);
				for(Invoice invoice: invoices) {
					emails = Arrays.asList("kfiolek@marketplacebrands.com","mkoziarski@marketplacebrands.com");
					body.put("invoiceNumber", invoice.getNumber());
					notificationService.sendMail(emails, body, Notification.TYPE.INVOICE_CREATED);
				}
	
			}
	
		}
	}
	
	@Component
	public class ShipmentListener {
		
		@Autowired
		private ShipmentReceiver shipmentReceiver;
		
		@JmsListener(destination = "shipmentUpdated", containerFactory = "myFactory")
		public void shipmentUpdatedEvent(JmsEntityMessage message) {
			MpbTenantContext.setCurrentTenant(message.getTenant());
			shipmentReceiver.shipmentUpdatedEvent(message);
		}
	}
	
}
