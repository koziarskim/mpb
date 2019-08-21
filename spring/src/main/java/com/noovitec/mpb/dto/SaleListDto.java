package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleListDto {
	
	private Long id;
	private String number;
	private String customerName;
	private Date date;
	private String dc;
}
