package com.noovitec.mpb.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.service.NotificationService;

import jms.JmsEmailMessage;

@Component
public class MpbJmsReceiver {
	
	private final Logger log = LoggerFactory.getLogger(MpbJmsReceiver.class);
	@Autowired
	NotificationService notificationService;
	@Autowired
	private VelocityEngine velocityEngine;

	@JmsListener(destination = "emailNotification", containerFactory = "myFactory")
	public void receiveMessage(JmsEmailMessage message) {
		MpbTenantContext.setCurrentTenant(message.getTenant());
		this.sendMail(message.getEmails(), message.getModel(), message.getType());
	}
	
	private void sendMail(List<String> emails, Map<String, String> model, Notification.TYPE type) {
		log.info("Sending notification: "+type);
		try {
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
			email.setText(body, "UTF-8");
			email.writeTo(buffer);
			byte[] bytes = buffer.toByteArray();
			String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
			Message message = new Message();
			message.setRaw(encodedEmail);
			if(!MpbRequestContext.getStaticSetting().isSkipNotification()) {
				message = service.users().messages().send("me", message).execute();
			}
		} catch (MessagingException | IOException | GeneralSecurityException e){
			e.printStackTrace();
		}
	}

}
