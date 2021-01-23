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

	public enum STATUS {DRAFT, READY, APPROVED, SCHEDULED, PRODUCED, ASSIGNED, SHIPPED, CANCELED, PAID}
	public enum UNITS {NOT_APPROVED, NOT_SCHEDULED, NOT_PRODUCED, NOT_ASSIGNED, NOT_SHIPPED, NOT_PAID}
	public enum CUSTOM_FILTER {NOT_PAID, PC_NOT_READY}

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
	private long unitsShipped;
	private long unitsAdjusted;
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
	private boolean pcr;
	
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
		this.unitsAdjusted = 0;
		this.unitsAssigned = 0;
		this.invoicedAmount = BigDecimal.ZERO;
		for (SaleItem si : this.getSaleItems()) {
			si.updateUnits();
			this.unitsSold += si.getUnits();
			this.unitsScheduled += si.getUnitsScheduled();
			this.unitsProduced += si.getUnitsProduced();
			this.unitsShipped += si.getUnitsShipped();
			this.unitsAdjusted += si.getUnitsAdjusted();
			this.unitsAssigned += si.getUnitsAssigned();
			if(si.getInvoicedAmount()!=null) {
				this.invoicedAmount = this.invoicedAmount.add(si.getInvoicedAmount());
			}
		}
		this.updateStatus();
	}
	
	private void updateStatus() {
		this.status = STATUS.DRAFT.name();
		if(this.isPendingApproval()) {
			status = STATUS.READY.name();
		}
		if(this.isApproved()) {
			this.status = Sale.STATUS.APPROVED.name();
		}
		if((this.unitsSold + this.unitsAdjusted) > 0 && (this.unitsSold + this.unitsAdjusted) == this.unitsScheduled) {
			this.status = Sale.STATUS.SCHEDULED.name();
		}
		if((this.unitsSold + this.unitsAdjusted) > 0 && (this.unitsSold + this.unitsAdjusted) == this.unitsProduced ) {
			this.status = Sale.STATUS.PRODUCED.name();
		}
		if((this.unitsSold + this.unitsAdjusted) > 0 && (this.unitsSold + this.unitsAdjusted) == this.unitsAssigned ) {
			this.status = Sale.STATUS.ASSIGNED.name();
		}
		if((this.unitsSold + this.unitsAdjusted) > 0 && (this.unitsSold + this.unitsAdjusted) == this.unitsShipped) {
			status = Sale.STATUS.SHIPPED.name();
		}
		if(this.isPaidInFull()) {
			status = STATUS.PAID.name();
		}
		if(this.isCancelled()) {
			status = Sale.STATUS.CANCELED.name();
		}

	}
}