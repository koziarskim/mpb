package com.noovitec.mpb.app;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MpbHibernateInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MpbHibernateInterceptor.class);


	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		log.info("MpbHibernateInterceptor onSave "+entity.getClass());
		return false;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		log.info("MpbHibernateInterceptor onFlushDirty");
		return false;
	}

}