package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListDto {

	private Long id;
	private String number;
	private LocalDate date;
	private LocalDate shippingDate;
	private String type;
	private String shipmentNumber;
	private boolean sent;
}
