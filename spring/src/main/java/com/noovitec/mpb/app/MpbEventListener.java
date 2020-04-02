package com.noovitec.mpb.app;

import java.lang.reflect.Field;

import javax.transaction.Transactional;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.service.NotificationService;

@Component
public class MpbEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

	private static final long serialVersionUID = 1L;
	public static final MpbEventListener INSTANCE = new MpbEventListener();
	private final Logger log = LoggerFactory.getLogger(MpbEventListener.class);
	@Autowired
	NotificationService notificationService;
	
	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Async
	@Transactional
	@Override
	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();
        Field[] fields = event.getEntity().getClass().getDeclaredFields();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        Object[] newStates = event.getState();
		if (entity.getClass() == Shipment.class) {
			notificationService.shipmentReady(entity, newStates, null, propertyNames);
			notificationService.shipmentShipped(entity, newStates, null, propertyNames);
		}
		if (entity.getClass() == Customer.class) {
			notificationService.customerShipped(entity, newStates, null, propertyNames);
		}
	}
	
	@Async
	@Transactional
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		Object entity = event.getEntity();
		Field[] fields = event.getEntity().getClass().getDeclaredFields();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        Object[] newStates = event.getState();
        Object[] oldStates = event.getOldState();
		if (entity.getClass() == Shipment.class) {
			notificationService.shipmentReady(entity, newStates, oldStates, propertyNames);
			notificationService.shipmentShipped(entity, newStates, oldStates, propertyNames);
		}
		if (entity.getClass() == Customer.class) {
			notificationService.customerShipped(entity, newStates, oldStates, propertyNames);
		}
	}

	@Async
	@Transactional
	@Override
	public void onPostDelete(PostDeleteEvent event) {
		Object entity = event.getEntity();
		Field[] fields = event.getEntity().getClass().getDeclaredFields();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
	}

}
