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
	private int unitsOnStack = 0; //This is the stack of Item not components.
	private int totalScheduled = 0; //Total Items scheduled not produced yet. Produced units are subtracted.
	private int unitsReadyProduction = 0; //Items ready for production (all components are on stack).
	private int unitsReadySchedule = 0; //Items ready to be scheduled (some components might be in transit).

	public ItemDto(Long id, String number, String name, String brand, String category, String status, Long unitsOnStack, Long totalScheduled) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.status = status;
		this.unitsOnStack = unitsOnStack==null?0:unitsOnStack.intValue();
		this.totalScheduled = totalScheduled==null?0:totalScheduled.intValue();
	}
}
