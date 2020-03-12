package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEventDto {
	
	private Long id;
	private String number;
	private String customer;
	private String dc;
	private String city;
	private String state;
	private String load;
	private Long pallets;
	private String start;
	private String end;
	private String klass = "mpb-default-event";
	
}
