package com.noovitec.mpb.app;

import java.lang.reflect.Field;

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
//				if (entity.getClass() == Sale.class) {
//					notificationService.saleShipped(entity, newStates, null, propertyNames);
//				}
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
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
				MpbTenantContext.setCurrentTenant(tenant);
		        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
		        Object[] newStates = event.getState();
		        Object[] oldStates = event.getOldState();
				if (entity.getClass() == Shipment.class) {
					notificationService.shipmentReady(entity, newStates, oldStates, propertyNames);
					notificationService.shipmentShipped(entity, newStates, oldStates, propertyNames);
				}
//				if (entity.getClass() == Sale.class) {
//					notificationService.saleShipped(entity, newStates, oldStates, propertyNames);
//				}
				if (entity.getClass() == Sale.class) {
					notificationService.salePendingApproval(entity, newStates, oldStates, propertyNames);
				}
				if (entity.getClass() == Customer.class) {
					notificationService.customerShipped(entity, newStates, oldStates, propertyNames);
				}
				MpbTenantContext.clear();
            }
        });
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		String tenant = MpbTenantContext.getCurrentTenant();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
            	MpbTenantContext.setCurrentTenant(tenant);
				Object entity = event.getEntity();
				Field[] fields = event.getEntity().getClass().getDeclaredFields();
		        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
		        MpbTenantContext.clear();
            }
        });
	}

}
