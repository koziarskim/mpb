package com.noovitec.mpb.jms.message;

import java.time.LocalDate;

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
public class JmsShipmentMessage extends JmsMessage {

	private static final long serialVersionUID = -5369798288520560425L;
	private boolean oldReady;
	private boolean ready;
	private LocalDate oldShippedDate;
	private LocalDate shippedDate;

}
