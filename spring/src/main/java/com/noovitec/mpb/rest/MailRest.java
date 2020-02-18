package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

	private final Logger log = LoggerFactory.getLogger(MailRest.class);
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS);
	private static final String USCG_JSON_SERVICE_CREDENTIAL_FILE_PATH = "mail/oauth-project-268605-d9f44c2e6932.json";
	private static final String USCG_P12_SERVICE_CREDENTIAL_FILE_PATH = "mail/mims-268617-499ba269d364.p12";
	private static final String MIMS_JSON_KEY = "mail/mims-268617-0ecb1dec6a2e.json";

	@Autowired
	HttpServletRequest request;

	@PostMapping("/mail")
	ResponseEntity<?> post() throws URISyntaxException, GeneralSecurityException, IOException, MessagingException {
		HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		GoogleCredential credential = this.getJsonCredentials(HTTP_TRANSPORT, JSON_FACTORY);
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("whatever").build();
		InternetAddress to = new InternetAddress("koziarskim@yahoo.com", "M K");
//		InternetAddress from = new InternetAddress("m@m.com", "Some Name");
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage email = new MimeMessage(session);
//		email.setFrom(from);
		email.addRecipient(javax.mail.Message.RecipientType.TO, to);
		email.setSubject("Subject here");
		email.setText("body here");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		byte[] bytes = buffer.toByteArray();
		String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
		Message message = new Message();
		message.setRaw(encodedEmail);
		message = service.users().messages().send("me", message).execute();
		System.out.println("Message id: " + message.getId());
		System.out.println(message.toPrettyString());
		return ResponseEntity.ok().build();
	}

	private GoogleCredential getJsonCredentials(HttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) throws GeneralSecurityException, IOException {
		InputStream credentialsJSON = this.getClass().getClassLoader().getResourceAsStream(USCG_JSON_SERVICE_CREDENTIAL_FILE_PATH);
//		InputStream credentialsJSON = this.getClass().getClassLoader().getResourceAsStream(MIMS_JSON_KEY);
		GoogleCredential gcFromJson = GoogleCredential.fromStream(credentialsJSON, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
		return new GoogleCredential.Builder()
				.setTransport(gcFromJson.getTransport())
				.setJsonFactory(gcFromJson.getJsonFactory())
				.setServiceAccountId(gcFromJson.getServiceAccountId())
				.setServiceAccountUser("mims@uscgcaptains.com")
//				.setServiceAccountUser("mims@marketplacebrands.com")
				.setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
				.setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
				.build();
	}

	private GoogleCredential getP12Credentials(HttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) throws GeneralSecurityException, IOException {
		// Use the client ID when making the OAuth 2.0 access token request (see
		// Google's OAuth 2.0 Service Account documentation).
		String serviceAccountClientID = "105603679281239253431";
		// Use the email address when granting the service account access to supported
		// Google APIs
		String serviceAccountUserEmail = "mims@marketplacebrands.com";
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(serviceAccountClientID) // requesting the token
				.setServiceAccountPrivateKeyFromP12File(new File(USCG_P12_SERVICE_CREDENTIAL_FILE_PATH)).setServiceAccountScopes(SCOPES) // see
																																			// https://developers.google.com/gmail/api/auth/scopes
				.setServiceAccountUser(serviceAccountUserEmail).build();
		credential.refreshToken();
		return credential;
	}

}