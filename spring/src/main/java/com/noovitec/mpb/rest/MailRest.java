package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.service.NotificationService;

@RestController
@RequestMapping("/api")
class MailRest {

	@Autowired
	VelocityEngine velocityEngine;
	@Autowired
	NotificationService notificationService;

	private final Logger log = LoggerFactory.getLogger(MailRest.class);

	@PostMapping("/mail")
	ResponseEntity<?> post() throws URISyntaxException, GeneralSecurityException, IOException, MessagingException {
		List<String> emails = new ArrayList<String>();
		emails.add("koziarskim@yahoo.com");
		Map<String, String> model = new HashMap<String, String>();
		model.put("customerName", "1234");
		String file = "File content";
		notificationService.sendMailAttachment(emails, model, Notification.TYPE.CUSTOMER_CREATED, file.getBytes(), "invoices.txt");
//		notificationService.sendMail(emails, model, Notification.TYPE.CUSTOMER_CREATED);
		return ResponseEntity.ok().build();
	}

}