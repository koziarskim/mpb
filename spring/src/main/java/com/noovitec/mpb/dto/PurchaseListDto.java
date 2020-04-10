package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseListDto {
	
	private Long id;
	private String number;
	private String name;
	private String invoiceNumber;
	private String supplierName;
	private LocalDate poDate;
	private LocalDate etaDate;
	private LocalDate shippingDate;
	private long unitsOrdered;
	private long unitsReceived;
	
}
