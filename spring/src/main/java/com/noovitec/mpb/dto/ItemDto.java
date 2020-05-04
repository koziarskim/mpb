package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

	private Long id;
	private String number;
	private String name;
	private String brandName;
	private String categoryName;
	private long unitsOnStock = 0;
	private long unitsSold = 0;
	private long unitsScheduled = 0;
	private long unitsProduced = 0;
	private long unitsReturned = 0;
	private long unitsShipped = 0;
	private long unitsAdjusted = 0;
}
