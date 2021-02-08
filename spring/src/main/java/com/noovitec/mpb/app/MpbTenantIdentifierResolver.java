package com.noovitec.mpb.app;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MpbTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
	
	private final Logger log = LoggerFactory.getLogger(MpbTenantIdentifierResolver.class);
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = MpbTenantContext.getCurrentTenant();
        if (tenantId == null) {
            tenantId = "public";
        }
        return tenantId;
    }
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
