package com.noovitec.mpb.app;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MpbHibernateListenerConfiguration {
     
    @PersistenceUnit
    private EntityManagerFactory emf;
     
    @Autowired
    private MpbUpdateEventListener updateListener;
     
    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
//        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(listener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(updateListener);
//        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(listener);
    }
}
