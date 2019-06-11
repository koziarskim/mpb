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
	private Long unitsOnStack = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;

}
