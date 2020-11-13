package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
public class Sale extends BaseEntity {

	private static final long serialVersionUID = 3963121563435423199L;

	public enum STATUS {
		DRAFT, PENDING_APPROVAL, APPROVED, PENDING_PROD, PENDING_SHIPMENT, SHIPPED, INVOICED_FULL, CANCELLED, PAID
	}

	private LocalDate date;
	private String number;
	private String name;
	private String paymentTerms;
	private String freightTerms;
	private LocalDate expectedDate;
	private BigDecimal totalPrice;
	private BigDecimal invoicedAmount;
	private long unitsProduced;
	private long unitsSold;
	private long unitsScheduled;
	private long unitsTransferedTo;
	private long unitsTransferedFrom;
	private long unitsShipped;
	private long unitsAdjusted;
	private long unitsOnStock;
	private long unitsAssigned;
	private String status;
	private boolean approved;
	private boolean pendingApproval;
	private boolean cancelled;
	private boolean paidInFull;
	private LocalDateTime modifiedDate;
	private LocalDate shippingFrom;
	private LocalDate shippingTo;
	private String notes;
	
	@JsonIgnoreProperties(value = { "sales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shippingAddress;

	@JsonIgnoreProperties(value = { "sale" }, allowSetters = true)
	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "sale_attachment", joinColumns = @JoinColumn(name = "sale_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	@OrderBy("created DESC")
	private Collection<Attachment> attachments = new HashSet<Attachment>();

	public void updateUnits() {
		this.unitsSold = 0;
		this.unitsScheduled = 0;
		this.unitsProduced = 0;
		this.unitsShipped = 0;
		this.unitsTransferedTo = 0;
		this.unitsTransferedFrom = 0;
		this.unitsAdjusted = 0;
		this.unitsOnStock = 0;
		this.unitsAssigned = 0;
		this.invoicedAmount = BigDecimal.ZERO;
		for (SaleItem si : this.getSaleItems()) {
			si.updateUnits();
			this.unitsSold += si.getUnits();
			this.unitsScheduled += si.getUnitsScheduled();
			this.unitsProduced += si.getUnitsProduced();
			this.unitsShipped += si.getUnitsShipped();
			this.unitsTransferedTo += si.getUnitsTransferedTo();
			this.unitsTransferedFrom += si.getUnitsTransferedFrom();
			this.unitsAdjusted += si.getUnitsAdjusted();
			this.unitsOnStock += si.getUnitsOnStock();
			this.unitsAssigned += si.getUnitsAssigned();
			if(si.getInvoicedAmount()!=null) {
				this.invoicedAmount = this.invoicedAmount.add(si.getInvoicedAmount());
			}
		}
//		this.unitsOnStock = this.unitsProduced + this.unitsTransferedTo - this.unitsTransferedFrom - this.unitsShipped + this.unitsReturned;
		this.updateStatus();
	}
	
	private void updateStatus() {
		this.status = STATUS.DRAFT.name();
		if(this.pendingApproval) {
			status = STATUS.PENDING_APPROVAL.name();
		}
		if(this.approved) {
			this.status = STATUS.APPROVED.name();
		}
//		if(this.unitsScheduled > 0 && this.unitsProduced < this.unitsScheduled) {
//			this.status = STATUS.PENDING_PROD.name();
//		}
		if(this.getUnitsOnStock() > 0 && this.getUnitsOnStock() >= (this.unitsSold + this.unitsAdjusted)) {
			this.status = STATUS.PENDING_SHIPMENT.name();
		}
		if((this.unitsSold + this.unitsAdjusted) > 0 && this.unitsShipped >= (this.unitsSold + this.unitsAdjusted)) {
			status = STATUS.SHIPPED.name();
		}
//		if(!this.totalPrice.equals(BigDecimal.ZERO) && !this.invoicedAmount.equals(BigDecimal.ZERO) && this.invoicedAmount.compareTo(this.totalPrice) >= 0) {
//			status = STATUS.INVOICED_FULL.name();
//		}
		if(this.cancelled) {
			status = STATUS.CANCELLED.name();
		}
		if(this.paidInFull) {
			status = STATUS.PAID.name();
		}
	}
}