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
	private Long units;
	private Long unitsOrdered;//TODO: do we need it?
	private BigDecimal unitPrice;
	private boolean selected;
	
	public ComponentDto(Long id, String number, String name, Long units, BigDecimal unitPrice, boolean selected) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.units = units;
		this.unitPrice = unitPrice;
		this.selected = selected;
	}
	
	@Transient
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		return this.getUnitPrice().multiply(BigDecimal.valueOf(units));
	}
	
}
