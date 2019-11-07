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
	private Long unitsSold;
	private Long unitsProduced;
	private Long unitsShipped;
	private Long unitsOnStock; //this.unitsProduced - this..unitsShipped;
	
}
