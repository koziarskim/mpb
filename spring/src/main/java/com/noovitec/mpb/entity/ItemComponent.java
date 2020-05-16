package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemComponent extends BaseEntity {

	private static final long serialVersionUID = 1769855029579272138L;

	private BigDecimal units = BigDecimal.ZERO;
	private long unitsReadyProd = 0;

	@JsonIgnoreProperties(value = { "itemComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "itemComponents", "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;
	
	public void updateUnits() {
		this.unitsReadyProd = 999999999;
		long units = BigDecimal.valueOf(this.getComponent().getUnitsOnStock() - this.getComponent().getUnitsLocked())
				.divide(this.getUnits(),0, RoundingMode.CEILING).longValue();
		if(units < this.unitsReadyProd) {
			this.unitsReadyProd = units<0?0:units;
		}
	}
}