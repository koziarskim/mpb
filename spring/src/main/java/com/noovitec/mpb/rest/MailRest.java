package com.noovitec.mpb.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.compress.utils.IOUtils;
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
		InternetAddress[] bcc = { new InternetAddress("mkoziarski@marketplacebrands.com"), };
		InternetAddress[] cc = { new InternetAddress("koziarskim@yahoo.com"), };
		this.sendEmail(cc, bcc, subject, body);
		return ResponseEntity.ok().build();
	}

	private void sendEmail(InternetAddress[] cc, InternetAddress[] bcc, String subject, String body)
			throws GeneralSecurityException, IOException, AddressException, MessagingException {
		HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		InputStream credentialsJSON = this.getClass().getClassLoader().getResourceAsStream(MIMS_JSON_KEY);
		GoogleCredential gcFromJson = GoogleCredential.fromStream(credentialsJSON, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(gcFromJson.getTransport()).setJsonFactory(gcFromJson.getJsonFactory())
				.setServiceAccountId(gcFromJson.getServiceAccountId()).setServiceAccountUser("mims@marketplacebrands.com")
				.setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey()).setServiceAccountScopes(gcFromJson.getServiceAccountScopes()).build();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("MIMS").build();
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		Session session = Session.getDefaultInstance(new Properties());
//		MimeMessage email = new MimeMessage(session);
//		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress("mims@marketplacebrands.com"));
//		email.addRecipients(javax.mail.Message.RecipientType.BCC, bcc);
//		email.addRecipients(javax.mail.Message.RecipientType.CC, cc);
//		email.setSubject(subject);
//		email.setText(body, "UTF-8");
//		email.writeTo(buffer);
//		byte[] bytes = buffer.toByteArray();
//		String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
//		Message message = new Message();
//		message.setRaw(encodedEmail);
//
//		InputStream is = new ByteArrayInputStream("Testing...".getBytes(Charset.forName("UTF-8")));
//		MimeBodyPart mimeBodyPart = new MimeBodyPart();
//		mimeBodyPart.setContent(body, "text/plain");
//		Multipart multipart = new MimeMultipart();
//		multipart.addBodyPart(mimeBodyPart);
//		mimeBodyPart = new MimeBodyPart();
//		File file = new File("c:/invoice.txt");
//		if (!file.exists()) {
//			log.info("File not found");
//		}
//		DataSource source = new FileDataSource(file);
//		mimeBodyPart.setDataHandler(new DataHandler(source));
//		mimeBodyPart.setFileName("file.pdf");
//		multipart.addBodyPart(mimeBodyPart);
//		email.setContent(multipart);
		
		
		
		MimeMessage content = createEmailWithAttachment("mkoziarski@marketplacebrands.com", "mims@marketplacebrands.com", "Test Email", "It works", "c:/invoice.txt");
		Message message = createMessageWithEmail(content);
		message = service.users().messages().send("me", message).execute();
		System.out.println("Message id: " + message.getId());
	}

	private MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, String filePath) throws MessagingException, IOException {
//		File file = new File(filePath);
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session);
		Multipart multipart = new MimeMultipart();
		InternetAddress tAddress = new InternetAddress(to);
		InternetAddress fAddress = new InternetAddress(from);
		email.setFrom(fAddress);
		email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
		email.setSubject(subject);
//		if(!file.exists()) {
//			log.info("File not found");
//			log.info(file.getAbsolutePath());
//		}
//		if (file.exists()) {
			FileDataSource source = new FileDataSource(filePath);
			InputStream is = new ByteArrayInputStream("Testing...".getBytes(Charset.forName("UTF-8")));
//			DataSource ds = new InputStreamDataSource("text/plain", "test.txt", new ByteArrayInputStream("CONTENT INPUT STREAM".getBytes()));
			DataSource ds = new ByteArrayDataSource("Testingg...", "application/x-any");
			MimeBodyPart messageFilePart = new MimeBodyPart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			try {
				messageBodyPart.setText(bodyText);
				messageFilePart.setDataHandler(new DataHandler(ds));
				messageFilePart.setFileName("invoice.pdf");

				multipart.addBodyPart(messageBodyPart);
				multipart.addBodyPart(messageFilePart);
				email.setContent(multipart);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
//		} else
//			email.setText(bodyText);
//		email.addHeader("Content-Type", "text/plain");
		return email;
	}
	
	private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    email.writeTo(baos);
	    String encodedEmail = Base64.getUrlEncoder().encodeToString(baos.toByteArray());
	    Message message = new Message();
	    message.setRaw(encodedEmail);
	    return message;
	}

}


class InputStreamDataSource implements DataSource {
    String contentType;
    String name;

    byte[] fileData;

    public InputStreamDataSource(String contentType, String name, InputStream inputStream) throws IOException {
        this.contentType = contentType;
        this.name = name;
        /**
         * It seems DataSource will close inputStream and reopen it.
         * I converted inputStream to a byte array, so it won't be closed again.
         */
        fileData = IOUtils.toByteArray(inputStream);
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public InputStream getInputStream() throws IOException {
        /**
         * Convert byte array back to inputStream.
         */
        return new ByteArrayInputStream(fileData);
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }
}