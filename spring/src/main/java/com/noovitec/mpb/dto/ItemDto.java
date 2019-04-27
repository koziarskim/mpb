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
	private int unitsReady = 0;
	private int unitsInTransit = 0;
	private int unitsOnStack = 0;
	private int unitsScheduled = 0;
	private int unitsPastTransit = 0;
	private int unitsFutureTransit = 0;
	private int unitsShort = 0;
	private int unitsAvailable = 0;
	
	public ItemDto(Long id, String number, String name, String brand, String category, String status, Long unitsOnStack, Long unitsScheduled) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.status = status;
		this.unitsOnStack = unitsOnStack==null?0:unitsOnStack.intValue();
		this.unitsScheduled = unitsScheduled==null?0:unitsScheduled.intValue();
	}
	
	public int getUnitsShort() {
		int units = this.getUnitsFutureTransit() - this.getUnitsScheduled();
		this.unitsShort = units<0?units:0;
		return this.unitsShort * (-1);
	}
	
	public int getUnitsAvailable() {
		this.unitsAvailable = this.getUnitsPastTransit() - this.getUnitsShort();
		return this.unitsAvailable;
	}
}
