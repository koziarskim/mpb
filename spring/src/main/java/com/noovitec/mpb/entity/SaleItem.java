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
import com.noovitec.mpb.entity.Sale.STATUS;

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
	private long units = 0; // unitsSold.
	private long unitsOnStock = 0;
	private long unitsProduced = 0;
	private long unitsScheduled = 0;
	private long unitsShipped = 0;
	private long unitsTransferedTo = 0;
	private long unitsTransferedFrom = 0;
	private long unitsReturned = 0;
	private long unitsAdjusted = 0;
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
		this.unitsScheduled = 0;
		this.unitsProduced = 0;
		this.unitsShipped = 0;
		this.unitsTransferedTo = 0;
		this.unitsTransferedFrom = 0;
		this.unitsReturned = 0;
		
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
		this.unitsOnStock = this.unitsProduced + this.unitsTransferedTo - this.unitsTransferedFrom - this.unitsReturned;
		if(this.unitsOnStock < 0) {
			this.unitsOnStock = 0;
		}
		this.updateStatus();
	}
	
	private void updateStatus() {
		this.status = STATUS.DRAFT.name();
		if(this.getSale().isPendingApproval()) {
			status = STATUS.PENDING_APPROVAL.name();
		}
		if(this.getSale().isApproved()) {
			this.status = Sale.STATUS.APPROVED.name();
		}
		if(this.unitsScheduled > 0 && this.unitsProduced < this.unitsScheduled) {
			this.status = Sale.STATUS.PENDING_PROD.name();
		}
		if(this.getUnitsOnStock() > 0 && this.unitsOnStock >= (this.units + this.unitsAdjusted)) {
			this.status = Sale.STATUS.PENDING_SHIPMENT.name();
		}
		if(this.unitsShipped > 0 && this.unitsShipped >= (this.units + this.unitsAdjusted)) {
			status = Sale.STATUS.SHIPPED.name();
		}
		if(this.unitsShipped > 0 && this.unitsShipped >= (this.units + this.unitsAdjusted) && this.unitsOnStock > 0) {
			status = Sale.STATUS.PENDING_TRANSFER.name();
		}
	}


}