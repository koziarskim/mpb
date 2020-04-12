package com.noovitec.mpb.app;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class MpbTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = MpbTenantContext.getCurrentTenant();
        if (tenantId != null) {
            return tenantId;
        }
        return "public";
    }
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
