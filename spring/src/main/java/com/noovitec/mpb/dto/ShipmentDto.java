package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {
	
	private Long id;
	private Long customerId;
	private String number;
	private LocalDate shippingDate;
	private LocalDate shippedDate;
	private LocalDate modifiedDate;
	private String customerName;
	private boolean ready;
	
}
