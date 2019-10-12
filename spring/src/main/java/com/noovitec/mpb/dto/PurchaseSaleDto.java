package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSaleDto {
	
	private Long id;
	private String number;
	private String customerName;
	private Long unitsSold;
	private Long unitsProduced;
	private Long unitsOrdered;
	private Long unitsReceived;
}
