package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemDto {
	
	private Long id;
	private Long saleId;
	private String saleNumber;
	private String customerName;
	private Long units;
	private String label;
	
	public SaleItemDto(Long id, Long saleId, String saleNumber, String customerName, int units) {
		this.id = id;
		this.saleId = saleId;
		this.saleNumber = saleNumber;
		this.customerName = customerName;
		this.units = Long.valueOf(units);
	}
	
	public String getLabel() {
		this.label = this.saleNumber + " - " + this.customerName;
		return this.label;
	}
}
