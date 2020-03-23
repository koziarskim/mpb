package com.noovitec.mpb.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MpbTenantContext {
    private static Logger log = LoggerFactory.getLogger(MpbTenantContext.class.getName());
    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
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
}
