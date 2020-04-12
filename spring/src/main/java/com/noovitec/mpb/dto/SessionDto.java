package com.noovitec.mpb.dto;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.entity.BaseEntity;

public class SessionDto {
	
	private Map<Long, BaseEntity> entityMap = new HashMap<Long, BaseEntity>();
	private String tenant;
	private SettingDto setting;
	
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
	
	public SettingDto getSetting() {
		if(this.setting==null) {
			try {
				String settingPath = System.getenv("MPB_SETTING");
				if(settingPath == null) {
					throw new Exception("MPB_SETTING not found/set");
				}
				ObjectMapper mapper = new ObjectMapper();
				setting = mapper.readValue(new File(settingPath), SettingDto.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		return this.setting;
	}
	
	public void setSetting(SettingDto setting) {
		this.setting = setting;
	}
}
