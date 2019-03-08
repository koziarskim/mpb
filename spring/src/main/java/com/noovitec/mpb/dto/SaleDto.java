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
}
