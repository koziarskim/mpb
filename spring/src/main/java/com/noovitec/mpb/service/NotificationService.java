package com.noovitec.mpb.service;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void shipmentReady(Long shipmentId, boolean prevReady, boolean ready);
	public void shipmentShipped(Long shipmentId, LocalDate prevShippedDate, LocalDate shippedDate);


	@Transactional
	@Service("notificationServiceImpl")
	public class NotificationServiceImpl implements NotificationService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		private NotificationRepo notificationRepo;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		public void shipmentReady(Long shipmentId, boolean prevReady, boolean ready) {
			if(!prevReady && ready) {
				log.info("Sending shipmentReady notification");
			}
		}
		
		public void shipmentShipped(Long shipmentId, LocalDate prevShippedDate, LocalDate shippedDate) {
			if(prevShippedDate == null && shippedDate !=null) {
				log.info("Sending shipmentShipped notification");
			}
		}

		
	}
}
