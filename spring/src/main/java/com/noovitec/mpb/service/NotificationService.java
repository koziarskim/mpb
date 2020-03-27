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
import org.springframework.scheduling.annotation.Async;
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
import com.noovitec.mpb.entity.BaseEntity;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void shipmentReady(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
	public void shipmentShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames);
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
		
		@Async
		@Transactional
		public void shipmentReady(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			List<String> emails = Arrays.asList("shipping@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
			boolean prevReady = previousState==null?false:((boolean) previousState[ArrayUtils.indexOf(propertyNames, "ready")]);
			if(!prevReady && shipment.isReady()) {
				Map<String, String> model = new HashMap<String, String>();
		        model.put("number", shipment.getNumber());
				this.sendMail(emails, "mail/shipmentReady.vm", model, shipment, Notification.TYPE.SHIPPING_READY.name());
			}

		}
		
		@Async
		@Transactional
		public void shipmentShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			List<String> emails = Arrays.asList("kzygulska@marketplacebrands.com", "kfiolek@marketplacebrands.com", "mkoziarski@marketplacebrands.com");
			LocalDate prevShippedDate = previousState==null?null:((LocalDate) previousState[ArrayUtils.indexOf(propertyNames, "shippedDate")]);
			if(prevShippedDate == null && shipment.getShippedDate() !=null) {
				Map<String, String> shipModel = new HashMap<String, String>();
				shipModel.put("number", shipment.getNumber());
				this.sendMail(emails, "mail/shipmentShipped.vm", shipModel, shipment, Notification.TYPE.SHIPPING_SHIPPED.name());
				List<Invoice> invoices = invoiceService.createInvoiceForShipment(shipment);
				for(Invoice invoice: invoices) {
					Map<String, String> invoiceModel = new HashMap<String, String>();
					invoiceModel.put("number", invoice.getNumber());
					this.sendMail(emails, "mail/invoiceCreated.vm", invoiceModel, invoice, Notification.TYPE.INVOICE_CREATED.name());
				}
			}
		}
		
		@Async
		@Transactional
		public void customerShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Customer customer = (Customer) entity;
			List<String> emails = Arrays.asList("kfiolek@marketplacebrands.com","hpyzikiewicz@marketplacebrands.com","mkoziarski@marketplacebrands.com");
			Long prevUnitsShipped = previousState==null?0:((Long) previousState[ArrayUtils.indexOf(propertyNames, "unitsShipped")]);
			if(prevUnitsShipped != customer.getUnitsShipped() && customer.getUnitsShipped() >= customer.getUnitsSold()) {
				Map<String, String> model = new HashMap<String, String>();
		        model.put("name", customer.getName());
				this.sendMail(emails, "mail/customerShipped.vm", model, customer, Notification.TYPE.CUSTOMER_SALES_SHIPPED.name());
			}
		}
		
		private void sendMail(List<String> emails, String template, Map<String, String> model, BaseEntity baseEntity, String type) {
			log.info("Sending notification: "+type);
			try {
				Notification notification = new Notification();
				notification.setEmails(emails.toString());
				notification.setEntity(baseEntity.getClass().getSimpleName());
				notification.setEntityId(baseEntity.getId());
				notification.setType(type);
				notification = notificationRepo.save(notification);
				List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
				String MIMS_JSON_KEY = "oauth/mims-268617-f7755598ac50.json";
		        model.put("notificationId", notification.getId().toString());
		        model.put("type", type);
		        String subject = "MIMS Notification";
				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
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
				message = service.users().messages().send("me", message).execute();
			} catch (MessagingException | IOException | GeneralSecurityException e){
				e.printStackTrace();
			}
		}
		
	}
}
