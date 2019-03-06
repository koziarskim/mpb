package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDto {
	
	public SaleDto(Long id) {
		this.id = id;
	}

	private Long id;
	private Date date;
	private String number;
	private String paymentTerms;
	private int freightTerms;
	private Date expectedDate;
	private boolean selected;
}
