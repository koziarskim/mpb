package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingListDto {
	
	private Long id;
	private String number;
	private String name;
	private Long purchaseId;
	private String purchaseNumber;
	private String purchaseName;
	private Long componentId;
	private String componentNumber;
	private String componentName;
	private String supplierName;
	private String invoiceNumber;
	private String containerNumber;
	private LocalDate receivedDate;
	private long unitsReceived;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private boolean extraUnits;
	
}
