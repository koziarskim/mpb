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
	
	private boolean none; //Bypass default constructor;
	
	public ItemListDto(Long id, String number, String name, String brand, String category, Long unitsOnStock, Long unitsSold, Long unitsScheduled, Long unitsProduced) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.brand = brand==null?null:brand;
		this.category = category==null?null:category;
		this.unitsOnStock = unitsOnStock==null?0L:unitsOnStock;
		this.unitsSold = unitsSold==null?0L:unitsSold;
		this.unitsScheduled = unitsScheduled==null?0L:unitsScheduled;
		this.unitsProduced = unitsProduced==null?0L:unitsProduced;
	}
}
