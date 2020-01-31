package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class SaleItem extends BaseEntity {

	private BigDecimal unitPrice = BigDecimal.ZERO;
	private BigDecimal totalUnitPrice = BigDecimal.ZERO;
	private int units; // unitsSold.
	private Long unitsProduced = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsShipped = 0L;
	private Long unitsTransferedTo = 0L;
	private Long unitsTransferedFrom = 0L;
	private String sku;

	@JsonIgnoreProperties(value = { "saleItems", "purchaseSales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_id", referencedColumnName = "id")
	private Sale sale;

	@JsonIgnoreProperties(value = { "saleItems", "itemComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "saleItem" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();

	@JsonIgnoreProperties(value = { "shipment" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ShipmentItem> shipmentItems = new HashSet<ShipmentItem>();

	@JsonIgnoreProperties(value = { "saleItemTo", "saleItemFrom" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sale_item_from_id")
	private Collection<SaleItemTransfer> transfersFrom = new HashSet<SaleItemTransfer>();

	@JsonIgnoreProperties(value = { "saleItemTo", "saleItemFrom" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sale_item_to_id")
	private Collection<SaleItemTransfer> transfersTo = new HashSet<SaleItemTransfer>();

	@Transient
	private Long unitsOnStock = 0L;

	public Long getUnitsOnStock() {
		return this.getUnitsProduced() + this.unitsTransferedTo - this.unitsTransferedFrom - this.getUnitsShipped();
	}

	public void updateUnits() {
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		this.unitsTransferedTo = 0L;
		this.unitsTransferedFrom = 0L;
		for (ScheduleEvent se : this.getScheduleEvents()) {
			se.updateUnits();
			this.unitsScheduled += se.getUnitsScheduled();
			this.unitsProduced += se.getUnitsProduced();
		}
		for (ShipmentItem si : this.getShipmentItems()) {
			this.unitsShipped += si.getUnits();
		}
		for (SaleItemTransfer sit: this.getTransfersFrom()) {
			this.unitsTransferedFrom -= sit.getUnitsTransfered();
		}
		for (SaleItemTransfer sit: this.getTransfersTo()) {
			this.unitsTransferedTo += sit.getUnitsTransfered();
		}
	}

}