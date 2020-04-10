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
	private String supplierName;
	private Long supplierId;
	private Long unitsSoldNotProd;
	private Long unitsOnStock;
	private Long unitsPendingPo;
	private Long unitsLocked;
	private Long unitsShort;
	private BigDecimal unitCost;
	private Long unitsForSale;
	private Long unitsForProduction;
	private Long unitsReceived;
}
