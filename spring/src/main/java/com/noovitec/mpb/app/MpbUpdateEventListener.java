package com.noovitec.mpb.app;

import java.lang.reflect.Field;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.entity.Sale;

@Component
public class MpbUpdateEventListener implements PostUpdateEventListener {

	private static final long serialVersionUID = 1L;
	public static final MpbUpdateEventListener INSTANCE = new MpbUpdateEventListener();
	private final Logger log = LoggerFactory.getLogger(MpbUpdateEventListener.class);
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
	    final Object entity = event.getEntity();
        Field[] fields = event.getEntity().getClass().getDeclaredFields();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        Object[] newStates = event.getState();
        Object[] oldStates = event.getOldState();
	
	    if(entity instanceof Sale) {
	    	Sale sale = (Sale) entity;
	    	log.info("onPostUpdate: "+sale.getId());
			Long prevUnitsShipped = oldStates==null?0:((Long) oldStates[ArrayUtils.indexOf(propertyNames, "unitsShipped")]);
			Long unitsShipped = sale.getUnitsShipped()==null?0L:sale.getUnitsShipped();
			Long unitsSold = sale.getUnitsSold()==null?0L:sale.getUnitsSold();
			if(prevUnitsShipped != unitsShipped && unitsSold > 0 && unitsShipped >= unitsSold) {
				
			}

	    }
	}
	
	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		// TODO Auto-generated method stub
		return false;
	}

}
