package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDto {
	
	public SaleDto(Long id, String number, Date date, String customerName, String dc, boolean selected) {
		this.id = id;
		this.date = date;
		this.number = number;
		this.customerName = customerName;
		this.dc = dc;
		this.selected = selected;
	}

	private Long id;
	private Date date;
	private String number;
	private String customerName;
	private String dc;
	private boolean selected;
}
