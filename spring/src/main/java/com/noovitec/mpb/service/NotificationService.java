package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void shipmentReady(Long shipmentId);


	@Transactional
	@Service("notificationServiceImpl")
	public class NotificationServiceImpl implements NotificationService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		private NotificationRepo notificationRepo;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		public void shipmentReady(Long shipmentId) {
			
		}

		
	}
}
