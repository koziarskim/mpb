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
import java.util.UUID;

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
		VelocityEngine velocityEngine;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		@Async
		public void shipmentReady(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			List<String> emails = Arrays.asList("mkoziarski@marketplacebrands.com");
			boolean prevReady = (boolean) previousState[ArrayUtils.indexOf(propertyNames, "ready")];
			if(!prevReady && shipment.isReady()) {
				Map<String, String> model = new HashMap<String, String>();
		        model.put("name", "Marcin");
				this.sendMail(emails, "mail/genericTemplate.vm", model, shipment, Notification.TYPE.SHIPPING_READY.name());
			}

		}
		
		@Async
		public void shipmentShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Shipment shipment = (Shipment) entity;
			List<String> emails = Arrays.asList("mkoziarski@marketplacebrands.com");
			LocalDate prevShippedDate = (LocalDate) previousState[ArrayUtils.indexOf(propertyNames, "shippedDate")];
			if(prevShippedDate == null && shipment.getShippedDate() !=null) {
				Map<String, String> model = new HashMap<String, String>();
		        model.put("name", "Marcin");
				this.sendMail(emails, "mail/genericTemplate.vm", model, shipment, Notification.TYPE.SHIPPING_SHIPPED.name());
			}
		}
		
		@Async
		public void customerShipped(Object entity, Object[] currentState, Object[] previousState, String[] propertyNames) {
			Customer customer = (Customer) entity;
			List<String> emails = Arrays.asList("mkoziarski@marketplacebrands.com");
			Long prevUnitsShipped = (Long) previousState[ArrayUtils.indexOf(propertyNames, "unitsShipped")];
			if(prevUnitsShipped != customer.getUnitsShipped() && customer.getUnitsShipped() >= customer.getUnitsSold()) {
				Map<String, String> model = new HashMap<String, String>();
		        model.put("name", "Marcin");
				this.sendMail(emails, "mail/genericTemplate.vm", model, customer, Notification.TYPE.CUSTOMER_SALES_SHIPPED.name());
			}
		}
		
		private String sendMail(List<String> emails, String template, Map<String, String> model, BaseEntity baseEntity, String type) {
			log.info("Sending notification: "+type);
			String notificationId = UUID.randomUUID().toString();
			try {
				List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
				String MIMS_JSON_KEY = "oauth/mims-268617-f7755598ac50.json";
		        model.put("notificationId", notificationId);
		        String subject = "MIMS Notification";
				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, model);
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
				String skipNotification = System.getenv("MPB_SKIP_NOTIFICATION");
				if(skipNotification!=null && skipNotification.equalsIgnoreCase("YES")) {
					notificationId = "SKIP_"+notificationId;
				}
				message = service.users().messages().send("me", message).execute();
				Notification notification = new Notification();
				notification.setNumber(notificationId);
				notification.setEmails(emails.toString());
				notification.setEntity(baseEntity.getClass().getSimpleName());
				notification.setEntityId(baseEntity.getId());
				notification.setType(type);
				notificationRepo.save(notification);
			} catch (MessagingException | IOException | GeneralSecurityException e){
				e.printStackTrace();
			}
			return notificationId;
		}
		
	}
}
