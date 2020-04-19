package com.noovitec.mpb.jms.message;

import java.io.Serializable;

import com.noovitec.mpb.app.MpbTenantContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JmsCustomerMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private long oldUnitsShipped;
	private long unitsShipped;
	private long unitsSold;
	@Builder.Default
	private String tenant = MpbTenantContext.getCurrentTenant();

}
