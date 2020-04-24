package com.noovitec.mpb.jms.message;

import java.io.Serializable;

import com.noovitec.mpb.app.MpbTenantContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JmsMessage implements Serializable{

	private static final long serialVersionUID = 2621949284279146593L;
	protected Long id;
	@Builder.Default
	private String tenant = MpbTenantContext.getCurrentTenant();

}
