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

import jdk.internal.jline.internal.Log;
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

	private static final long serialVersionUID = -6345762674113542595L;
	private BigDecimal unitPrice;
	private BigDecimal totalUnitPrice;
	private long units; // unitsSold.
	private long unitsAssigned;
	
	private long unitsOnStock;
	private long unitsProduced;
	private long unitsScheduled;
	private long unitsShipped;
	private long unitsTransferedTo;
	private long unitsTransferedFrom;
	private long unitsAdjusted;

	private BigDecimal invoicedAmount;
	private String sku;
	private String status;
	
	@JsonIgnoreProperties(value = { "saleItems", "purchaseSales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_id", referencedColumnName = "id")
	private Sale sale;

	@JsonIgnoreProperties(value = { "saleItems", "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_packaging_id", referencedColumnName = "id")
	private ItemPackaging itemPackaging;


	@JsonIgnoreProperties(value = { "saleItem", "item" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();

	@JsonIgnoreProperties(value = { "saleItem", "shipment" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ShipmentItem> shipmentItems = new HashSet<ShipmentItem>();

	@JsonIgnoreProperties(value = { "saleItem", "invoice" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<InvoiceItem> invoiceItems = new HashSet<InvoiceItem>();

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
		this.unitsOnStock = 0;
		this.invoicedAmount = BigDecimal.ZERO;
		
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
		for(InvoiceItem ii : this.invoiceItems) {
			if(ii.getTotalUnitPrice()!=null && ii.getInvoice().isSent()) {
				this.invoicedAmount = this.invoicedAmount.add(ii.getTotalUnitPrice());
			}
		}
//		unitsAssigned += this.getUnitsProduced() + (this.getUnitsTransferedTo() - this.getUnitsTransferedFrom());
		this.unitsOnStock = this.unitsProduced + this.unitsTransferedTo - this.unitsTransferedFrom - this.unitsShipped;
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
//		if(this.unitsScheduled > 0 && this.unitsProduced < this.unitsScheduled) {
//			this.status = Sale.STATUS.PENDING_PROD.name();
//		}
		if((this.units + this.unitsAdjusted) > 0 && this.unitsAssigned == (this.units + this.unitsAdjusted)) {
			this.status = Sale.STATUS.PENDING_SHIPMENT.name();
		}
		if((this.units + this.unitsAdjusted) > 0 && this.unitsShipped >= (this.units + this.unitsAdjusted)) {
			status = Sale.STATUS.SHIPPED.name();
		}
//		if(!this.totalUnitPrice.equals(BigDecimal.ZERO) && !this.invoicedAmount.equals(BigDecimal.ZERO) && this.invoicedAmount.compareTo(this.totalUnitPrice) >= 0) {
//			status = STATUS.INVOICED_FULL.name();
//		}
		if(this.getSale().isCancelled()) {
			status = Sale.STATUS.CANCELLED.name();
		}
		if(this.getSale().isPaidInFull()) {
			status = STATUS.PAID.name();
		}
	}
}