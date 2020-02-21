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
	private Long unitsOnStock = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsProduced = 0L;
	private Long unitsShipped = 0L;
	private Long unitsReadyProd = 0L;
	private Long performance = 0L;
}
