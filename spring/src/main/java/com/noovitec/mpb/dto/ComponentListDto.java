package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentListDto {

	private Long id;
	private String number;
	private String name;
	private String categoryName;
	private String componentTypeName;
	private String supplierName;
	private Long supplierId;
	
	private Long unitsSoldAdj;
	private Long unitsPurchased;
	private Long unitsReceived;
	private Long unitsProduced;
	private Long unitsShipped;
	private Long unitsAssigned;
	private Long unitsAdjusted;
	private Long unitsOnStock;
	private Long unitsOnFloor;
	private Long unitsPendAssignment;
	private Long unitsPendReceiving;
	private Long unitsExtra;
	
	 public ComponentListDto(Object...fields) {
	        super();
	        this.id = ((BigInteger) fields[0]).longValue();
	        this.number = (String) fields[1];
	        this.name = (String) fields[2];
	        this.categoryName = (String) fields[3];
	        this.componentTypeName = (String) fields[4];
	        this.supplierName = (String) fields[5];
	        this.supplierId = fields[6]==null?null:((BigInteger) fields[6]).longValue();
	        this.unitsSoldAdj = fields[7]==null?null:((BigDecimal) fields[7]).longValue();
	        this.unitsPurchased = fields[8]==null?null:((BigDecimal) fields[8]).longValue();
	        this.unitsReceived = fields[9]==null?null:((BigDecimal) fields[9]).longValue();
	        this.unitsProduced = fields[10]==null?null:((BigDecimal) fields[10]).longValue();
	        this.unitsShipped = fields[11]==null?null:((BigDecimal) fields[11]).longValue();
	        this.unitsAssigned = fields[12]==null?null:((BigDecimal) fields[12]).longValue();
	        this.unitsAdjusted = fields[13]==null?null:((BigDecimal) fields[13]).longValue();
	        this.unitsOnStock = fields[14]==null?null:((BigDecimal) fields[14]).longValue();
	        this.unitsOnFloor = fields[15]==null?null:((BigDecimal) fields[15]).longValue();
	        this.unitsPendAssignment = fields[16]==null?null:((BigDecimal) fields[16]).longValue();
	        this.unitsPendReceiving = fields[17]==null?null:((BigDecimal) fields[17]).longValue();
	        this.unitsExtra = fields[18]==null?null:((BigDecimal) fields[18]).longValue();
	    }
}
