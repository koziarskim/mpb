package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentDto {

	private Long id;
	private String number;
	private String name;
	private String itemNumber;
	private String itemName;
	private String saleNumber;
	private Date saleDate;
	private boolean selected;
}
