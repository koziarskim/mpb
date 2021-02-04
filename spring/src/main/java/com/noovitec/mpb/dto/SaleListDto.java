package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleListDto {
	
	private Long id;
	private String number;
	private String name;
	private String customerName;
	private LocalDate date;
	private String dc;
	private long unitsSold;
	private long unitsOnStock;
	private long unitsScheduled;
	private long unitsProduced;
	private long unitsShipped;
	private long unitsTransferedTo;
	private long unitsTransferedFrom;
	private long unitsAdjusted;
	private long unitsAssigned;
	private BigDecimal invoicedAmount;
	private List<?> saleItems;
	private String status;
	private String shippingWindow;
	
}
