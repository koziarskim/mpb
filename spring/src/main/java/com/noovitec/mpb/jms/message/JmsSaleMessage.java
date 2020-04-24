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
public class JmsSaleMessage extends JmsMessage{

	private static final long serialVersionUID = 7451408489022265233L;
	private boolean oldPendingApproval;
	private boolean pendingApproval;
	private long oldUnitsShipped;
	private long unitsShipped;
	private long unitsSold;

}
