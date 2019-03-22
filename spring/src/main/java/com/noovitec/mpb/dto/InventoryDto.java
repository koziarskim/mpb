package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

	private Long componentId;
	private String componentNumber;
	private String componentName;
	private String supplierName;
	private int unitsOnStack;
	private int unitsOrdered;

}
