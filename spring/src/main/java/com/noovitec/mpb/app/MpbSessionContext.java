package com.noovitec.mpb.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.noovitec.mpb.entity.BaseEntity;

@Component
@SessionScope
public class MpbSessionContext {
	
	private Map<Long, BaseEntity> entityMap = new HashMap<Long, BaseEntity>();
	private String tenant;
	
	public MpbSessionContext() {
		this.tenant = MpbTenantContext.getCurrentTenant();
	}
	
	public List<BaseEntity> getList(){
		List<BaseEntity> list = new ArrayList<BaseEntity>(entityMap.values());
		return list;
	}
	
	public void add(BaseEntity baseEntity) {
		this.entityMap.put(baseEntity.getId(), baseEntity);
	}
	
	public BaseEntity get(Long entityId) {
		return this.entityMap.get(entityId);
	}
	
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	
	public String getTenant() {
		return this.tenant;
	}
}
