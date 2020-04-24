package com.noovitec.mpb.jms.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JmsCustomerMessage extends JmsMessage {

	private static final long serialVersionUID = -7353248898605902858L;
	private long oldUnitsShipped;
	private long unitsShipped;
	private long unitsSold;

}
