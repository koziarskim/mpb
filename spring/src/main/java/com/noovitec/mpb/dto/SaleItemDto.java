package com.noovitec.mpb.dto;

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
	private Long unitsSold;
	private Long unitsScheduled;
	private Long unitsProduced;
	private Long unitsShipped;
	private Long unitsOnStock;
	private Long unitsTransferedTo;
	private Long unitsTranferedFrom;
	private Long unitsAdjusted;
	private String status;
	
}
