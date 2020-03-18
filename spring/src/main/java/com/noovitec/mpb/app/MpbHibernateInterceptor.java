package com.noovitec.mpb.app;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.service.NotificationService;

public class MpbHibernateInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MpbHibernateInterceptor.class);
	@Autowired
	NotificationService notificationService;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		log.info("MpbHibernateInterceptor onSave "+entity.getClass());
		return false;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		log.info("MpbHibernateInterceptor onFlushDirty "+entity.getClass());
		if(entity.getClass() == Shipment.class) {
			Shipment shipment = (Shipment) entity;
			this.shipmentUpdated(shipment, previousState, propertyNames);
		}
		return false;
	}
	
	private void shipmentUpdated(Shipment shipment, Object[] previousState, String[] propertyNames) {
		boolean prevReady = (boolean) previousState[ArrayUtils.indexOf(propertyNames, "ready")];
		if(!prevReady && shipment.isReady()) {
			notificationService.shipmentReady(shipment.getId());
		}
//		boolean prevReady = (boolean) previousState[ArrayUtils.indexOf(propertyNames, "ready")];
//		if(!prevReady && shipment.isReady()) {
//			notificationService.shipmentReady(shipment.getId());
//		}
	}

}