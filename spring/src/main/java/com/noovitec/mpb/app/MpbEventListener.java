package com.noovitec.mpb.app;

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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.entity.BaseEntity;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.jms.message.JmsEntityMessage;
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
    private JmsTemplate jmsTemplate;
    
	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onPostInsert(PostInsertEvent event) {
		String tenant = MpbTenantContext.getCurrentTenant();
		Object entity = event.getEntity();
		BaseEntity baseEntity = null;
		if(entity.getClass().isAssignableFrom(BaseEntity.class)) {
			return;
		}
		baseEntity = (BaseEntity) entity;
		if(baseEntity.isDirty()) {
			return;	
		}
		baseEntity.setDirty(true);
		if (entity.getClass() != Shipment.class && entity.getClass() != Sale.class && entity.getClass() != Customer.class) {
			return;
		}
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
            	MpbTenantContext.setCurrentTenant(tenant);
		        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
		        Object[] newStates = event.getState();
				if (entity.getClass() == Shipment.class) {
					notificationService.shipmentReady(entity, newStates, null, propertyNames);
					notificationService.shipmentShipped(entity, newStates, null, propertyNames);
				}
				if (entity.getClass() == Sale.class) {
					notificationService.saleShipped(entity, newStates, null, propertyNames);
				}
				if (entity.getClass() == Sale.class) {
					notificationService.salePendingApproval(entity, newStates, null, propertyNames);
				}
				if (entity.getClass() == Customer.class) {
					notificationService.customerShipped(entity, newStates, null, propertyNames);
				}
				MpbTenantContext.clear();
            }
        });
	}
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		Object entity = event.getEntity();
		if (entity.getClass() != Shipment.class && entity.getClass() != Sale.class && entity.getClass() != Customer.class) {
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
    	JmsEntityMessage message = JmsEntityMessage.builder().id((Long) event.getId())
    			.state(event.getState()).oldState(event.getOldState())
    			.propertyNames(event.getPersister().getEntityPersister().getPropertyNames()).build();
		if (entity.getClass() == Shipment.class) {
			jmsTemplate.convertAndSend("shipmentUpdated", message);
		}
		if (entity.getClass() == Sale.class) {
			jmsTemplate.convertAndSend("saleUpdated", message);
		}
//				if (entity.getClass() == Sale.class) {
//					notificationService.salePendingApproval(entity, newStates, oldStates, propertyNames);
//				}
//				if (entity.getClass() == Customer.class) {
//					notificationService.customerShipped(entity, newStates, oldStates, propertyNames);
//				}
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
