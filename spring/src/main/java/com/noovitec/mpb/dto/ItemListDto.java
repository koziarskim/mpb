package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemListDto {

	private Long id;
	private String number;
	private String name;
	private String brand;
	private String category;
	private long unitsOnStock = 0;
	private long unitsSold = 0;
	private long unitsScheduled = 0;
	private long unitsProduced = 0;
	private long unitsShipped = 0;
	private long unitsReadyProd = 0;
	private long unitsOverstock = 0;
	private long unitsAdjusted = 0;
	private long performance = 0;
}
