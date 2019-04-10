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
	private Long unitsNeeded;
	private Long unitsOnStack;
	private Long units;
	private Long unitsReceived;
	private BigDecimal unitPrice;
	private boolean selected;
	
	public ComponentDto(Long id, String number, String name, Long unitsNeeded, int unitsOnStack, Long units, Long unitsReceived, BigDecimal unitPrice, boolean selected) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.unitsNeeded = unitsNeeded;
		this.unitsOnStack = Long.valueOf(unitsOnStack);
		this.units = units==null?0:units;
		this.unitPrice = unitPrice;
		this.selected = selected;
		this.unitsReceived = unitsReceived==null?0L:unitsReceived;
	}
	
	@Transient
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		return this.getUnitPrice().multiply(BigDecimal.valueOf(this.units==null?1:this.units));
	}
	
}
