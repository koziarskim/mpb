package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemListDto {

	private Long id;
	private String number;
	private String itemNumber;
	private String itemName;
	private String saleNumber;
	private LocalDate date;
	private LocalDate shippingDate;
	private String type;
	private String shipmentNumber;
	private boolean sent;
	private String customerName;
}
