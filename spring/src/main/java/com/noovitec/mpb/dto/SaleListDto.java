package com.noovitec.mpb.dto;

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
	private Long unitsSold;
	private Long unitsOnStock;
	private Long unitsScheduled;
	private Long unitsProduced;
	private Long unitsShipped;
	private Long unitsTransferedTo;
	private Long unitsTransferedFrom;
	private Long unitsAdjusted;
	private List<?> saleItems;
	private String status;
	private String shippingWindow;
	
}
