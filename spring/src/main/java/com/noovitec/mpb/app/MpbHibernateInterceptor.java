package com.noovitec.mpb.app;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.service.NotificationService;

public class MpbHibernateInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MpbHibernateInterceptor.class);

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		NotificationService notificationService = MpbApplicationContext.getBean(NotificationService.class);
		if (entity.getClass() == Shipment.class) {
			notificationService.shipmentReady(entity, currentState, previousState, propertyNames);
			notificationService.shipmentShipped(entity, currentState, previousState, propertyNames);
		}
		if (entity.getClass() == Customer.class) {
			notificationService.customerShipped(entity, currentState, previousState, propertyNames);
		}
		return false;
	}

}