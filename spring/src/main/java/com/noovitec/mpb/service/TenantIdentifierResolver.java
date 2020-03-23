package com.noovitec.mpb.service;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import com.noovitec.mpb.app.MpbTenantContext;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
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
