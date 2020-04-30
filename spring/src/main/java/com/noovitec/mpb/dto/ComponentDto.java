package com.noovitec.mpb.dto;

import java.math.BigDecimal;

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
	private String categoryName;
	private String componentTypeName;
	private String supplierName;
	private long casePack;
	private Long supplierId;
	private Long unitsSoldNotProd;
	private Long unitsOnStock;
	private Long unitsOrdered;
	private Long unitsLocked;
	private Long unitsShort;
	private BigDecimal unitCost;
	private Long unitsForSale;
	private Long unitsForProduction;
	private Long unitsReceived;
}
