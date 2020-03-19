package com.noovitec.mpb.service;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.repo.NotificationRepo;

public interface NotificationService {
	
	public void shipmentReady(Object[] currentState, Object[] previousState, String[] propertyNames);
	public void shipmentShipped(Object[] currentState, Object[] previousState, String[] propertyNames);
	public void customerShipped(Object[] currentState, Object[] previousState, String[] propertyNames);


	@Transactional
	@Service("notificationServiceImpl")
	public class NotificationServiceImpl implements NotificationService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		private NotificationRepo notificationRepo;

		public NotificationServiceImpl(NotificationRepo notificationRepo) {
			this.notificationRepo = notificationRepo;
		}
		
		public void shipmentReady(Object[] currentState, Object[] previousState, String[] propertyNames) {
			boolean prevReady = (boolean) previousState[ArrayUtils.indexOf(propertyNames, "ready")];
			boolean ready = (boolean) currentState[ArrayUtils.indexOf(propertyNames, "ready")];
			if(!prevReady && ready) {
				log.info("Sending shipmentReady notification");
			}
			Notification notification = new Notification();
			notificationRepo.save(notification);
		}
		
		public void shipmentShipped(Object[] currentState, Object[] previousState, String[] propertyNames) {
			LocalDate prevShippedDate = (LocalDate) previousState[ArrayUtils.indexOf(propertyNames, "shippedDate")];
			LocalDate shippedDate = (LocalDate) currentState[ArrayUtils.indexOf(propertyNames, "shippedDate")];
			if(prevShippedDate == null && shippedDate !=null) {
				log.info("Sending shipmentShipped notification");
			}
		}
		
		public void customerShipped(Object[] currentState, Object[] previousState, String[] propertyNames) {
			Long prevUnitsShipped = (Long) previousState[ArrayUtils.indexOf(propertyNames, "unitsShipped")];
			Long unitsShipped = (Long) currentState[ArrayUtils.indexOf(propertyNames, "unitsShipped")];
			if(prevUnitsShipped != unitsShipped) {
				Long unitsSold = (Long) currentState[ArrayUtils.indexOf(propertyNames, "unitsSold")];
				if(unitsShipped >= unitsSold) {
					log.info("Sending customerShipped notification");
				}
			}
		}
		
	}
}
