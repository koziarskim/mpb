package com.noovitec.mpb.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;

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
import com.noovitec.mpb.app.MpbRequestContext;
import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void sendMail(List<String> emails, Map<String, String> model, Notification.TYPE type);
	public void sendMailAttachment(List<String> emails, Map<String, String> model, Notification.TYPE type, byte[] file, String fileName);
	
	@Transactional
	@Service("notificationServiceImpl")
	public class NotificationServiceImpl implements NotificationService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		private NotificationRepo notificationRepo;
		@Autowired
		private VelocityEngine velocityEngine;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		public void sendMail(List<String> emails, Map<String, String> model, Notification.TYPE type) {
			this.sendMailAttachment(emails, model, type, null, null);
		}
		
		public void sendMailAttachment(List<String> emails, Map<String, String> model, Notification.TYPE type, byte[] file, String fileName) {
			log.info("EmailNotification: "+type);
			try {
				emails.add("mkoziarski@marketplacebrands.com");
				Notification notification = new Notification();
				notification.setEmails(emails.toString());
				notification.setType(type.name());
				notification = notificationRepo.save(notification);
				List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
				String MIMS_JSON_KEY = "oauth/mims-268617-f7755598ac50.json";
		        model.put("type", type.name());
		        model.put("yearContext", MpbTenantContext.getCurrentTenant().replace("y", ""));
		        String subject = "MIMS Notification";
				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, type.template(), model);
				if(MpbRequestContext.getStaticSetting().isDevEnv()) {
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
				Multipart multipart = new MimeMultipart();
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(body);
				multipart.addBodyPart(messageBodyPart);
				if(file != null) {
					DataSource ds = new ByteArrayDataSource(file, "application/x-any");
					MimeBodyPart messageFilePart = new MimeBodyPart();
					messageFilePart.setDataHandler(new DataHandler(ds));
					messageFilePart.setFileName(fileName);
					multipart.addBodyPart(messageFilePart);
				}
				email.setContent(multipart);
				email.writeTo(buffer);
				byte[] bytes = buffer.toByteArray();
				String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
				Message message = new Message();
				message.setRaw(encodedEmail);
				if(!MpbRequestContext.getStaticSetting().isSkipNotification()) {
					message = service.users().messages().send("me", message).execute();
				}else {
					log.info("Email sent skip");
				}
			} catch (MessagingException | IOException | GeneralSecurityException e){
				e.printStackTrace();
			}
		}
	}
}
