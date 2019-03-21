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
	private int unitsOnStock;
	private Long purchaseId;
	private String purchaseNumber;
	private int unitsOrdered;

}
