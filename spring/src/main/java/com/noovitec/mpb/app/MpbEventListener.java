package com.noovitec.mpb.app;

import java.time.LocalDate;
import java.time.LocalTime;

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
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.entity.BaseEntity;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.jms.message.JmsSaleMessage;
import com.noovitec.mpb.jms.message.JmsShipmentMessage;
import com.noovitec.mpb.jms.message.JmsUtil;
import com.noovitec.mpb.service.NotificationService;

@Component
public class MpbEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

	private static final long serialVersionUID = 1L;
	public static final MpbEventListener INSTANCE = new MpbEventListener();
	private final Logger log = LoggerFactory.getLogger(MpbEventListener.class);
	@Autowired
	NotificationService notificationService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private JmsUtil jmsUtil;
    
	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		return false;
	}
	
	@Override
	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();
		if (entity.getClass() != Shipment.class) {
			return;
		}
		BaseEntity baseEntity = null;
		if(entity.getClass().isAssignableFrom(BaseEntity.class)) {
			return;
		}
		baseEntity = (BaseEntity) entity;
		if(baseEntity.isDirty()) {
			return;	
		}
		baseEntity.setDirty(true);
		String[] keys = event.getPersister().getEntityPersister().getPropertyNames();
		Object[] state = event.getState();
		if (entity.getClass() == Shipment.class) {
			JmsShipmentMessage message = JmsShipmentMessage.builder().id((Long) event.getId())
	    			.oldReady(jmsUtil.getBoolean("ready", keys, null))
	    			.ready(jmsUtil.getBoolean("ready", keys, state))
	    			.oldShippedDate(jmsUtil.getLocalDate("shippedDate", keys, null))
	    			.shippedDate(jmsUtil.getLocalDate("shippedDate", keys, state))
	    			.type("shipmentUpdated")
	    			.build();
			MpbTenantContext.addMessage(message);
		}
	}
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		Object entity = event.getEntity();
		if (entity.getClass() != Shipment.class) {
			return;
		}
		BaseEntity baseEntity = null;
		if(entity.getClass().isAssignableFrom(BaseEntity.class)) {
			return;
		}
		baseEntity = (BaseEntity) entity;
		if(baseEntity.isDirty()) {
			return;	
		}
		baseEntity.setDirty(true);
		String[] keys = event.getPersister().getEntityPersister().getPropertyNames();
		Object[] oldState = event.getOldState();
		Object[] state = event.getState();
		if (entity.getClass() == Shipment.class) {
			LocalDate oldShippingDate = jmsUtil.getLocalDate("shippingDate", keys, oldState);
			LocalDate shippingDate = jmsUtil.getLocalDate("shippingDate", keys, state);
			LocalTime oldShippingTime = jmsUtil.getLocalTime("shippingTime", keys, oldState);
			LocalTime shippingTime = jmsUtil.getLocalTime("shippingTime", keys, state);
			boolean shippingDateChanged = false;
			if(oldShippingDate!=null && shippingDate!=null && !oldShippingDate.equals(shippingDate)) {
				shippingDateChanged = true;
			}
			if(oldShippingTime!=null && shippingTime!=null && !oldShippingTime.equals(shippingTime)) {
				shippingDateChanged = true;
			}
			JmsShipmentMessage message = JmsShipmentMessage.builder().id((Long) event.getId())
	    			.oldReady(jmsUtil.getBoolean("ready", keys, oldState))
	    			.ready(jmsUtil.getBoolean("ready", keys, state))
	    			.oldShippedDate(jmsUtil.getLocalDate("shippedDate", keys, oldState))
	    			.shippedDate(jmsUtil.getLocalDate("shippedDate", keys, state))
	    			.shippingDateChanged(shippingDateChanged)
	    			.type("shipmentUpdated")
	    			.build();
			MpbTenantContext.addMessage(message);
		}
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		String tenant = MpbTenantContext.getCurrentTenant();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
            	MpbTenantContext.setCurrentTenant(tenant);
				Object entity = event.getEntity();
				Object[] oldState = event.getDeletedState();
		        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
		        MpbTenantContext.clear();
            }
        });
	}
	
}
