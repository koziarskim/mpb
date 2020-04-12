package com.noovitec.mpb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.SettingDto;
import com.noovitec.mpb.entity.BaseEntity;

@Component
@SessionScope
public class MpbRequestContext {
	
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
		//TODO: Force to load new file.
		this.setting = null;
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
	
	public static SettingDto getStaticSetting() {
		SettingDto setting = null;
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
		return setting;
	}
	
}
