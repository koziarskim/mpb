package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
	
	private Long id;
	private String name;
	private String account;
	private String city;
	private String phone;
}
