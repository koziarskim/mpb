package com.noovitec.mpb.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.jms.message.JmsMessage;

public class MpbTenantContext {
    private static Logger log = LoggerFactory.getLogger(MpbTenantContext.class.getName());
    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static ThreadLocal<List<JmsMessage>> messages = new ThreadLocal<>();
    public static void setCurrentTenant(String tenant) {
        log.debug("Setting tenant to " + tenant);
        currentTenant.set(tenant);
    }
    public static String getCurrentTenant() {
        return currentTenant.get();
    }
    public static void clear() {
        currentTenant.set(null);
    }
    
    public static void addMessage(JmsMessage message) {
    	if(messages.get()==null) {
    		messages.set(new ArrayList<JmsMessage>());
    	}
        messages.get().add(message);
    }
    public static List<JmsMessage> getMessages() {
    	if(messages.get()==null) {
            messages.set(new ArrayList<JmsMessage>());
    	}
    	return messages.get();
    	
    }
    public static void clearMessages() {
        messages.set(null);
    }
}
