package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDto {
	
	private Long id;
	private Long saleId;
	private String saleName;
	private String saleNumber;
	private Long itemId;
	private String itemName;
	private String itemNumber;
	private Long customerId;
	private String customerName;
	private String dc;
	private long unitsSold;
	private long unitsScheduled;
	private long unitsProduced;
	private long unitsShipped;
	private long unitsOnStock;
	private long unitsTransferedTo;
	private long unitsTranferedFrom;
	private long unitsAdjusted;
	private long unitsAssigned;
	private BigDecimal invoicedAmount;
	private String status;
	private String packagingLabel;
	private boolean approved;
	
}
