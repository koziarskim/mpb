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
	
	public ItemDto(Long id, String number, String name, String brand, String category, String status) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.status = status;
	}
}
