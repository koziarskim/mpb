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
	private Long packagingId;
	private String itemName;
	private String itemNumber;
	private String label;
	private BigDecimal caseHeight;
	private BigDecimal caseLength;
	private BigDecimal caseWidth;
	private int casePack;
	private BigDecimal caseWeight;
	private BigDecimal palletWeight;
	private int ti;
	private int hi;
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private long unitsOnFloor;
	private long unitsOnStock;
	private long salesNotAssigned;
	private long unitsNotAssigned;
	private long unitsShort;
	private long unitsPenShip;
	private long salesOpen;
	private long unitsOpen;
	private long unitsProduced;
	private long unitsAssigned;
	private long unitsScheduled;
	
}
