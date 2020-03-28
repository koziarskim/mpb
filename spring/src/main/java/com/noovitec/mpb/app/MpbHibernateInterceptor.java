package com.noovitec.mpb.app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.service.InterceptorService;

public class MpbHibernateInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MpbHibernateInterceptor.class);

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] currentState, String[] propertyNames, Type[] types) {
		InterceptorService interceptorService = MpbApplicationContext.getBean(InterceptorService.class);
		interceptorService.onSave(entity, id, currentState, propertyNames, types);
		return false;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		InterceptorService interceptorService = MpbApplicationContext.getBean(InterceptorService.class);
		interceptorService.onFlush(entity, id, currentState, previousState, propertyNames, types);
		return false;
	}
	
}