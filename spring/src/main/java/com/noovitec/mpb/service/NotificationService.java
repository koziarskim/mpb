package com.noovitec.mpb.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.apache.commons.lang.ArrayUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.BaseEntity;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void shipmentReady(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
	public void shipmentShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
	public void saleShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
	public void salePendingApproval(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
	public void customerShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);


	@Transactional
	@Service("notificationServiceImpl")
	public class NotificationServiceImpl implements NotificationService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		private NotificationRepo notificationRepo;
		@Autowired
		private VelocityEngine velocityEngine;
		@Autowired
		private InvoiceService invoiceService;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		public void shipmentReady(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			boolean prevReady = false;
			if(previousState != null) {
				Object obj = previousState[ArrayUtils.indexOf(propertyNames, "ready")];
				prevReady = obj==null?false:((boolean) obj);
			}
			if(!prevReady && shipment.isReady()) {
				List<String> emails = Arrays.asList("shipping@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
				Map<String, String> model = new HashMap<String, String>();
		        model.put("shipmentNumber", shipment.getNumber());
				this.sendMail(emails, model, shipment, Notification.TYPE.SHIPPING_READY);
			}

		}
		
		public void shipmentShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			LocalDate prevShippedDate = null;
			if(previousState != null) {
				Object obj = previousState[ArrayUtils.indexOf(propertyNames, "shippedDate")];
				if(obj!=null) {
					prevShippedDate = obj==null?null:((LocalDate) obj);
				}
			}
			if(prevShippedDate == null && shipment.getShippedDate() !=null) {
				List<String> emails = Arrays.asList("kzygulska@marketplacebrands.com", "kfiolek@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
				Map<String, String> shipModel = new HashMap<String, String>();
				shipModel.put("shipmentNumber", shipment.getNumber());
				this.sendMail(emails, shipModel, shipment, Notification.TYPE.SHIPPING_SHIPPED);
				List<Invoice> invoices = invoiceService.createInvoiceForShipment(shipment);
				for(Invoice invoice: invoices) {
					List<String> invoiceEmails = Arrays.asList("kfiolek@marketplacebrands.com","mkoziarski@marketplacebrands.com");
					Map<String, String> invoiceModel = new HashMap<String, String>();
		        	invoiceModel.put("invoiceNumber", invoice.getNumber());
					this.sendMail(invoiceEmails, invoiceModel, shipment, Notification.TYPE.INVOICE_CREATED);
				}

			}
		}

		public void saleShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Sale sale = (Sale) entity;
			long prevUnitsShipped = 0;
			if(previousState != null) {
				Object obj = previousState[ArrayUtils.indexOf(propertyNames, "unitsShipped")];
				prevUnitsShipped = obj==null?0L:((Long) obj);
			}
			Customer customer = sale.getCustomer();
			long unitsShipped = sale.getUnitsShipped();
			long unitsSold = sale.getUnitsSold();
			Invoice invoice = null;
			if(unitsShipped > 0 && prevUnitsShipped != unitsShipped && unitsSold > 0
					&& customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_FIRST_SHIPMENT.name())) {
				invoice = invoiceService.createInvoiceForSale(sale, customer.getInvoiceType());
			}
			if(unitsShipped > 0 && prevUnitsShipped != unitsShipped && unitsSold > 0 &&  unitsShipped >= unitsSold 
					&& customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_LAST_SHIPMENT.name())) {
				invoice = invoiceService.createInvoiceForSale(sale, customer.getInvoiceType());
			}
	        if(invoice!=null) {
				List<String> emails = Arrays.asList("kfiolek@marketplacebrands.com","mkoziarski@marketplacebrands.com");
				Map<String, String> model = new HashMap<String, String>();
	        	model.put("invoiceNumber", invoice.getNumber());
	        	this.sendMail(emails, model, sale, Notification.TYPE.INVOICE_CREATED);
	        }
		}
		
		public void salePendingApproval(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Sale sale = (Sale) entity;
			boolean prevPendingApproval = false;
			if(previousState != null) {
				Object obj = previousState[ArrayUtils.indexOf(propertyNames, "pendingApproval")];
				prevPendingApproval = obj==null?false:((boolean) obj);
			}
			boolean pendingApproval = sale.isPendingApproval();
			if(!prevPendingApproval && pendingApproval) {
				List<String> emails = Arrays.asList("vtomasik@marketplacebrands.com");
				Map<String, String> model = new HashMap<String, String>();
	        	model.put("saleNumber", sale.getNumber());
	        	this.sendMail(emails, model, sale, Notification.TYPE.SALE_PENDING);
			}
		}
		
		public void customerShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Customer customer = (Customer) entity;
			long prevUnitsShipped = 0;
			if(previousState != null) {
				Object obj = previousState[ArrayUtils.indexOf(propertyNames, "unitsShipped")];
				prevUnitsShipped = obj==null?0L:((Long) obj);
			}
			long unitsShipped = customer.getUnitsShipped();
			long unitsSold = customer.getUnitsSold();
			if(prevUnitsShipped != unitsShipped && unitsSold > 0 &&  unitsShipped >= unitsSold) {
				List<String> emails = Arrays.asList("hpyzikiewicz@marketplacebrands.com","mkoziarski@marketplacebrands.com");
				Map<String, String> model = new HashMap<String, String>();
		        model.put("customerName", customer.getName());
				this.sendMail(emails, model, customer, Notification.TYPE.CUSTOMER_SHIPPED);
			}
		}
		
		private void sendMail(List<String> emails, Map<String, String> model, BaseEntity baseEntity, Notification.TYPE type) {
			log.info("Sending notification: "+type);
			try {
				Notification notification = new Notification();
				notification.setEmails(emails.toString());
				notification.setEntity(baseEntity.getClass().getSimpleName());
				notification.setEntityId(baseEntity.getId());
				notification.setType(type.name());
				notification = notificationRepo.save(notification);
				List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
				String MIMS_JSON_KEY = "oauth/mims-268617-f7755598ac50.json";
		        model.put("notificationId", notification.getId().toString());
		        model.put("type", type.name());
		        model.put("yearContext", MpbTenantContext.getCurrentTenant().replace("y", ""));
		        String subject = "MIMS Notification";
				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, type.template(), model);
				String skipNotification = System.getenv("MPB_SKIP_NOTIFICATION");
				if(skipNotification!=null && skipNotification.equalsIgnoreCase("YES")) {
					emails = Arrays.asList("mkoziarski@marketplacebrands.com");
				}
				InternetAddress[] bcc = new InternetAddress[emails.size()]; 
			    for (int i =0; i < emails.size(); i++) 
			    	bcc[i] = new InternetAddress(emails.get(i)); 
				HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
				JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
				InputStream credentialsJSON = this.getClass().getClassLoader().getResourceAsStream(MIMS_JSON_KEY);
				GoogleCredential gcFromJson = GoogleCredential.fromStream(credentialsJSON, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
				GoogleCredential credential = new GoogleCredential.Builder()
						.setTransport(gcFromJson.getTransport())
						.setJsonFactory(gcFromJson.getJsonFactory())
						.setServiceAccountId(gcFromJson.getServiceAccountId())
						.setServiceAccountUser("mims@marketplacebrands.com")
						.setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
						.setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
						.build();
				Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("MIMS").build();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				Session session = Session.getDefaultInstance(new Properties());
				MimeMessage email = new MimeMessage(session);
				email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress("mims@marketplacebrands.com"));
				email.addRecipients(javax.mail.Message.RecipientType.BCC, bcc);
				email.setSubject(subject);
				email.setText(body, "UTF-8");
				email.writeTo(buffer);
				byte[] bytes = buffer.toByteArray();
				String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
				Message message = new Message();
				message.setRaw(encodedEmail);
//				message = service.users().messages().send("me", message).execute();
			} catch (MessagingException | IOException | GeneralSecurityException e){
				e.printStackTrace();
			}
		}
		
	}
}
