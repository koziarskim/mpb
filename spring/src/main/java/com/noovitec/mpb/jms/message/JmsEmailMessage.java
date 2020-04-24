package com.noovitec.mpb.jms.message;

import java.util.List;
import java.util.Map;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JmsEmailMessage extends JmsMessage {

	private static final long serialVersionUID = -6154862672106060690L;
	List<String> emails;
	Map<String, String> model;
	Notification.TYPE type;
	@Builder.Default
	String tenant = MpbTenantContext.getCurrentTenant();
}
