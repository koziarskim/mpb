package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPackagingListDto {
	
	private Long id;
	private Long itemId;
	private String itemName;
	private String itemNumber;
	private String name;
	private String typeLabel;
	private BigDecimal caseHeight;
	private BigDecimal caseWidth;
	private BigDecimal caseDepth;
	private int casePack;
	private BigDecimal palletWeight;
	private int ti;
	private int hi;
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private long unitsProduced = 0;
	private long unitsAssigned = 0;
	private long unitsOnStock = 0;
	private long unitsScheduled = 0;
	
}
