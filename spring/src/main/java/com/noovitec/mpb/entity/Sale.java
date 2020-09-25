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

	public enum STATUS {
		DRAFT, PENDING_APPROVAL, APPROVED, PENDING_PROD, PENDING_SHIPMENT, SHIPPED, CANCELLED, PAID
	}

	private LocalDate date;
	private String number;
	private String name;
	private String paymentTerms;
	private String freightTerms;
	private LocalDate expectedDate;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private long unitsProduced = 0 ;
	private long unitsSold = 0 ;
	private long unitsScheduled = 0;
	private long unitsTransferedTo = 0;
	private long unitsTransferedFrom = 0;
	private long unitsShipped = 0;
	private long unitsAdjusted = 0;
	private long unitsOnStock = 0;
	private long unitsReturned = 0;
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
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "sale_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "sale_attachment", joinColumns = @JoinColumn(name = "sale_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	@OrderBy("created DESC")
	private Collection<Attachment> attachments = new HashSet<Attachment>();

	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		this.unitsTransferedTo = 0L;
		this.unitsTransferedFrom = 0L;
		this.unitsAdjusted = 0L;
		this.unitsReturned = 0L;
		this.unitsOnStock = 0L;
		for (SaleItem sa : this.getSaleItems()) {
			sa.updateUnits();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
			this.unitsTransferedTo += sa.getUnitsTransferedTo();
			this.unitsTransferedFrom += sa.getUnitsTransferedFrom();
			this.unitsAdjusted += sa.getUnitsAdjusted();
			this.unitsReturned += sa.getUnitsReturned();
			this.unitsOnStock += sa.getUnitsOnStock();
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
		if(this.unitsScheduled > 0 && this.unitsProduced < this.unitsScheduled) {
			this.status = STATUS.PENDING_PROD.name();
		}
		if(this.getUnitsOnStock() > 0 && this.getUnitsOnStock() >= (this.unitsSold + this.unitsAdjusted)) {
			this.status = STATUS.PENDING_SHIPMENT.name();
		}
		if(this.unitsSold > 0 && this.unitsShipped >= (this.unitsSold + this.unitsAdjusted)) {
			status = STATUS.SHIPPED.name();
		}
		if(this.cancelled) {
			status = STATUS.CANCELLED.name();
		}
		if(this.paidInFull) {
			status = STATUS.PAID.name();
		}
		
	}

}