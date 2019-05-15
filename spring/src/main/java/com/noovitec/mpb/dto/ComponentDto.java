package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import javax.persistence.Transient;

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
	private String supplierName;
	private Long unitsNeeded; //For Sales included in PO (at the time PO was created).
	private Long unitsOnStack;
	private Long units;
	private Long unitsReceived; //For this PO.
	private Long unitsInTransit;
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private boolean selected;
	
	public ComponentDto(Long id, String number) {
		this.id = id;
		this.number = number;
	}
	
	public ComponentDto(Long id, String number, String name, Long unitsNeeded, int unitsOnStack, Long units, Long unitsReceived, Long unitsInTransit, BigDecimal unitPrice, boolean selected) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.unitsNeeded = unitsNeeded;
		this.unitsOnStack = Long.valueOf(unitsOnStack);
		this.units = units==null?0:units;
		this.unitPrice = unitPrice;
		this.selected = selected;
		this.unitsReceived = unitsReceived==null?0L:unitsReceived;
		this.unitsInTransit = unitsInTransit==null?0L:unitsInTransit;
	}
	
	@Transient
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		return this.getUnitPrice().multiply(BigDecimal.valueOf(this.units==null?1:this.units));
	}
	
}
