package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

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
	private Long unitsOnStock = 0L;
	private Long unitsProduced = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsShipped = 0L;
	private Long unitsTransferedTo = 0L;
	private Long unitsTransferedFrom = 0L;
	private Long unitsReturned = 0L;
	private Long unitsAdjusted = 0L;
	private String sku;
	private String status;
	
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

	@JsonIgnoreProperties(value = { "saleItem", "shipment" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ShipmentItem> shipmentItems = new HashSet<ShipmentItem>();

	@JsonIgnoreProperties(value = { "saleItem", "itemReturn" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<SaleItemReturn> saleItemReturns = new HashSet<SaleItemReturn>();

	@JsonIgnoreProperties(value = { "saleItemTo", "saleItemFrom" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sale_item_from_id")
	private Collection<SaleItemTransfer> transfersFrom = new HashSet<SaleItemTransfer>();

	@JsonIgnoreProperties(value = { "saleItemTo", "saleItemFrom" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sale_item_to_id")
	private Collection<SaleItemTransfer> transfersTo = new HashSet<SaleItemTransfer>();

	public void updateUnits() {
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		this.unitsTransferedTo = 0L;
		this.unitsTransferedFrom = 0L;
		this.unitsReturned = 0L;
		
		for(SaleItemReturn sir: this.getSaleItemReturns()) {
			this.unitsReturned += sir.getUnitsReturned();
		}
		for (ScheduleEvent se : this.getScheduleEvents()) {
			se.updateUnits();
			this.unitsScheduled += se.getUnitsScheduled();
			this.unitsProduced += se.getUnitsProduced();
		}
		for (ShipmentItem si : this.getShipmentItems()) {
			if(si.getShipment().getShippedDate() !=null ) {
				this.unitsShipped += si.getUnits();
			}
		}
		for (SaleItemTransfer sit: this.getTransfersFrom()) {
			this.unitsTransferedFrom += sit.getUnitsTransfered();
		}
		for (SaleItemTransfer sit: this.getTransfersTo()) {
			this.unitsTransferedTo += sit.getUnitsTransfered();
		}
		this.unitsOnStock = this.unitsProduced + this.unitsTransferedTo - this.unitsTransferedFrom - this.unitsShipped + this.unitsReturned;
		this.updateStatus();
	}
	
	private void updateStatus() {
		this.status = "PENDING_APPROVAL";
		if(this.getSale().isApproved()) {
			this.status = "APPROVED";
		}
		if(this.unitsScheduled > 0 && this.unitsProduced < this.unitsScheduled) {
			this.status = "PENDING_PROD";
		}
		if(this.getUnitsOnStock() > 0 && this.unitsOnStock >= (this.units + this.unitsAdjusted)) {
			this.status = "PENDING_SHIPMENT";
		}
		if(this.unitsShipped > 0 && this.unitsShipped >= (this.units + this.unitsAdjusted)) {
			status = "SHIPPED";
		}
		if(this.unitsShipped > 0 && this.unitsShipped >= (this.units + this.unitsAdjusted) && this.unitsOnStock > 0) {
			status = "PENDING_TRANSFER";
		}
	}


}