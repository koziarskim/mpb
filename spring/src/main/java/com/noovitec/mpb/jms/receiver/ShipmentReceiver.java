package com.noovitec.mpb.jms.receiver;

import java.time.LocalDate;
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
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.jms.message.JmsEntityMessage;
import com.noovitec.mpb.repo.ShipmentRepo;
import com.noovitec.mpb.service.InvoiceService;
import com.noovitec.mpb.service.NotificationService;

public interface ShipmentReceiver {
	public void shipmentUpdatedEvent(JmsEntityMessage message);
	
	@Transactional
	@Service("shipmentReceiverImpl")
	public class ShipmentReceiverImpl implements ShipmentReceiver{
		
		private final Logger log = LoggerFactory.getLogger(ShipmentReceiverImpl.class);
		@Autowired
		private NotificationService notificationService;
		@Autowired
		private InvoiceService invoiceService;
		@Autowired
		private ShipmentRepo shipmentRepo;
		@Autowired
		private JmsUtil jmsUtil;
	
		public void shipmentUpdatedEvent(JmsEntityMessage message) {
			List<String> emails = null;
			Map<String, String> body = new HashMap<String, String>();
			Shipment shipment = null;
			//Shipment is ready
			boolean ready = jmsUtil.getBoolean("ready", message.getPropertyNames(), message.getState());
			boolean prevReady = jmsUtil.getBoolean("ready", message.getPropertyNames(), message.getOldState());
			if(!prevReady && ready) {
				shipment = shipmentRepo.findById(message.getId()).get();
				emails = new ArrayList<>(Arrays.asList("shipping@marketplacebrands.com"));
				body.put("shipmentNumber", shipment.getNumber());
				notificationService.sendMail(emails, body, Notification.TYPE.SHIPPING_READY);
			}
			//Shipment is shipped;
			LocalDate shippedDate = jmsUtil.getLocalDate("shippedDate", message.getPropertyNames(), message.getState());
			LocalDate prevShippedDate = jmsUtil.getLocalDate("shippedDate", message.getPropertyNames(), message.getOldState());
			if(prevShippedDate == null && shippedDate !=null) {
				if(shipment==null) {
					shipment = shipmentRepo.findById(message.getId()).get();
				}
				emails = new ArrayList<>(Arrays.asList("kzygulska@marketplacebrands.com"));
				body.put("shipmentNumber", shipment.getNumber());
	        	notificationService.sendMail(emails, body, Notification.TYPE.SHIPPING_SHIPPED);
				List<Invoice> invoices = invoiceService.createInvoiceForShipment(shipment);
				for(Invoice invoice: invoices) {
					emails = new ArrayList<>(Arrays.asList("kfiolek@marketplacebrands.com"));
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
