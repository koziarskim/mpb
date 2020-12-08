package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

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
	private Double unitsShipped;
	private Long unitsReceived;
	private Long unitsProduced;
	private Long compOnFloor;
	private Long prodOnFloor;
	private Long unitsOnFloor;
	private BigDecimal averageUnitPrice;
	private Long totalFloorPrice;
	
	 public ComponentInventoryListDto(Object...fields) {
	        super();
	        this.id = ((BigInteger) fields[0]).longValue();
	        this.number = (String) fields[1];
	        this.name = (String) fields[2];
	        this.categoryName = (String) fields[3];
	        this.componentTypeName = (String) fields[4];
	        this.supplierName = (String) fields[5];
	        this.supplierId = ((BigInteger) fields[6]).longValue();
	        this.unitsShipped = fields[7]==null?null:((BigDecimal) fields[7]).doubleValue();
	        this.unitsReceived = fields[8]==null?null:((BigDecimal) fields[8]).longValue();
	        this.unitsProduced = fields[9]==null?null:((BigDecimal) fields[9]).longValue();
	        this.compOnFloor = fields[10]==null?null:((BigDecimal) fields[10]).longValue();
	        this.prodOnFloor = fields[11]==null?null:((BigDecimal) fields[11]).longValue();
	        this.unitsOnFloor = fields[12]==null?null:((BigDecimal) fields[12]).longValue();
	        this.averageUnitPrice = (BigDecimal) fields[13];
	        this.totalFloorPrice = fields[14]==null?null:((BigDecimal) fields[14]).longValue();

	    }
}
