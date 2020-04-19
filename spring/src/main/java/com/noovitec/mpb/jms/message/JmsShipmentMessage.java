package com.noovitec.mpb.jms.message;

import java.io.Serializable;
import java.time.LocalDate;

import com.noovitec.mpb.app.MpbTenantContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JmsShipmentMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean oldReady;
	private boolean ready;
	private LocalDate oldShippedDate;
	private LocalDate shippedDate;
	@Builder.Default
	private String tenant = MpbTenantContext.getCurrentTenant();

}
