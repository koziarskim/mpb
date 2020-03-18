package com.noovitec.mpb.app;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class HibernateInterceptor extends EmptyInterceptor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // do your checks here
        return false;
    }
}