package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentInventoryListDto {

	private Long id;
	private String number;
	private String name;
	private String categoryName;
	private String componentTypeName;
	private String supplierName;
	private Long supplierId;
	private Long unitsShipped;
	private Long unitsReceived;
	private Long unitsOnFloor;
	private BigDecimal averageUnitPrice;
	private Long totalFloorPrice;
}
