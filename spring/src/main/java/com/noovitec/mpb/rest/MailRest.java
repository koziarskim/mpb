package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

@RestController
@RequestMapping("/api")
class MailRest {

	@Autowired
	VelocityEngine velocityEngine;
	
	private final Logger log = LoggerFactory.getLogger(MailRest.class);
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
	private static final String MIMS_JSON_KEY = "oauth/mims-268617-f7755598ac50.json";

	@PostMapping("/mail")
	ResponseEntity<?> post() throws URISyntaxException, GeneralSecurityException, IOException, MessagingException {
		String notificationId = UUID.randomUUID().toString();
		Map<String, String> model = new HashMap<String, String>();
        model.put("name", "Marcin");
        model.put("notificationId", notificationId);
        String subject = "MIMS Notification (Test)";
		String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/genericTemplate.vm", model);
		InternetAddress[] bcc = {
				new InternetAddress("mkoziarski@marketplacebrands.com"),
				new InternetAddress("koziarskim@yahoo.com"),
//				new InternetAddress("akoziarski@marketplacebrands.com"),
//				new InternetAddress("hpyzikiewicz@marketplacebrands.com")
		};
		this.sendEmail(bcc, subject, body);
		return ResponseEntity.ok().build();
	}

	private void sendEmail(InternetAddress[] bcc, String subject, String body) throws GeneralSecurityException, IOException, AddressException, MessagingException {
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
		System.out.println("Message id: " + message.getId());
	}

}