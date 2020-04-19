package com.noovitec.mpb.jms.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JmsEmailMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	List<String> emails;
	Map<String, String> model;
	Notification.TYPE type;
	@Builder.Default
	String tenant = MpbTenantContext.getCurrentTenant();
}
