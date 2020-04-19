package com.noovitec.mpb.jms.message;

import java.io.Serializable;
import java.util.Map;

import com.noovitec.mpb.app.MpbTenantContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JmsEntityMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private Object[] state; 
	private Object[] oldState; 
	private String[] propertyNames;
	@Builder.Default
	private String tenant = MpbTenantContext.getCurrentTenant();
}
