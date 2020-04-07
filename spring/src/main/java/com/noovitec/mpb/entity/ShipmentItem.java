package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
public class ShipmentItem extends BaseEntity {

	private long units = 0;
	private long cases = 0;
	private long pallets = 0;
	private String instructions;

	@JsonIgnoreProperties(value = { "shipmentItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "shipment_id", referencedColumnName = "id")
	private Shipment shipment;

	@JsonIgnoreProperties(value = { "shipmentItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_id", referencedColumnName = "id")
	private SaleItem saleItem;
	
	@Transient
	private Long prevUnits = 0L;
	
	public Long getPrevUnits() {
		return this.units;
	}

}