package com.noovitec.mpb.service;

import java.io.Serializable;

import javax.transaction.Transactional;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;

public interface InterceptorService {
	
	public void onSave(Object entity, Serializable id, Object[] currentState, String[] propertyNames, Type[] types);
	public void onFlush(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);


	@Transactional
	@Service("interceptorServiceImpl")
	public class NotificationServiceImpl implements InterceptorService {

		private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
		NotificationService notificationService;

		public NotificationServiceImpl(NotificationService notificationService) {
			this.notificationService = notificationService;
		}
		
		@Async
		@Transactional
		public void onSave(Object entity, Serializable id, Object[] currentState, String[] propertyNames, Type[] types) {
			if (entity.getClass() == Shipment.class) {
				notificationService.shipmentReady(entity, currentState, null, propertyNames);
				notificationService.shipmentShipped(entity, currentState, null, propertyNames);
			}
			if (entity.getClass() == Customer.class) {
				notificationService.customerShipped(entity, currentState, null, propertyNames);
			}
//			if (entity.getClass() == Sale.class) {
//				notificationService.saleShipped(entity, currentState, null, propertyNames);
//			}
		}
		
		@Async
		@Transactional
		public void onFlush(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
			if (entity.getClass() == Shipment.class) {
				notificationService.shipmentReady(entity, currentState, previousState, propertyNames);
				notificationService.shipmentShipped(entity, currentState, previousState, propertyNames);
			}
			if (entity.getClass() == Customer.class) {
				notificationService.customerShipped(entity, currentState, previousState, propertyNames);
			}
//			if (entity.getClass() == Sale.class) {
//				notificationService.saleShipped(entity, currentState, previousState, propertyNames);
//			}
		}
		
	}
}
