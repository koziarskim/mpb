package com.noovitec.mpb.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.dto.JmsMessageDto;
import com.noovitec.mpb.entity.Year;
import com.noovitec.mpb.repo.YearRepo;
import com.noovitec.mpb.service.NotificationService;

@Component
public class MpbJmsReceiver {
	
	private final Logger log = LoggerFactory.getLogger(MpbJmsReceiver.class);
	@Autowired
	NotificationService notificationService;
	@Autowired
	YearRepo yearRepo;

	@JmsListener(destination = "notification", containerFactory = "myFactory")
	public void receiveMessage(JmsMessageDto message) {
		log.info("Tenant: "+message.getTenant());
		MpbTenantContext.setCurrentTenant(message.getTenant());
		log.info("Processing Message: "+message.getId()+": "+message.getKlas().getSimpleName());
		List<Year> years = yearRepo.findAll();
	}
}
