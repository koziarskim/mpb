package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagingListDto {
	
	private Long id;
	private String name;
	private String typeLabel;
	private BigDecimal caseHeight;
	private BigDecimal caseLength;
	private BigDecimal caseWidth;
	private int casePack;
	private BigDecimal palletWeight;
	private int ti;
	private int hi;
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	
}
