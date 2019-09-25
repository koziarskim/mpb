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
	private String brand;
	private String category;
	private String status;
	private Long unitsOnStack = 0L; //This is the stack of Item not components.
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsProduced = 0L;
}
