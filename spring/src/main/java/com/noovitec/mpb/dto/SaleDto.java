package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDto {
	
	private Long id;
	private String number;
	private Date date;
	private String customerName;
	private String dc;
	private boolean selected;
	private String label;
	
	public SaleDto(Long id, String number, Date date, String customerName, String dc, boolean selected) {
		this.id = id;
		this.number = number;
		this.date = date;
		this.customerName = customerName;
		this.dc = dc;
		this.selected = selected;
	}
	
	public SaleDto(Long id, String number, String customerName) {
		this.id = id;
		this.number = number;
		this.customerName = customerName;
	}
	
	public String getLabel() {
		this.label = this.number + " - " + this.customerName;
		return this.label;
	}
}
