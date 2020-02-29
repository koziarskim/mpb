package com.noovitec.mpb.entity;

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
public class SaleItemReturn extends BaseEntity{

	private Long unitsReturned;

	@JsonIgnoreProperties(value = { "saleItemReturns" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_return_id", referencedColumnName = "id")
	private ItemReturn itemReturn;

	@JsonIgnoreProperties(value = { "saleItemReturns", "shipmentItems", "scheduleEvents", "transfersFrom", "transfersTo" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_id", referencedColumnName = "id")
	private SaleItem saleItem;

}