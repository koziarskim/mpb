package com.noovitec.mpb.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.SettingDto;
import com.noovitec.mpb.jms.message.JmsMessage;

@Component
@SessionScope
public class MpbRequestContext {
	
	private List<JmsMessage> messages = new ArrayList<JmsMessage>();
	private String tenant;
	private SettingDto setting;
	
	public void addMessage(JmsMessage message) {
		this.messages.add(message);
	}
	
	public List<JmsMessage> getMessages(){
		return this.messages;
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
