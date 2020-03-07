package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class Sale extends BaseEntity {

	private LocalDate date;
	private String number;
	private String name;
	private String paymentTerms;
	private LocalDate expectedDate;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private Long unitsProduced = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsTransferedTo = 0L;
	private Long unitsTransferedFrom = 0L;
	private Long unitsShipped = 0L;
	private String status;
	private boolean approved;
	
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

	@Transient
	private Long unitsOnStock = 0L;

	public Long getUnitsOnStock() {
		return this.getUnitsProduced() + this.unitsTransferedTo - this.unitsTransferedFrom - this.getUnitsShipped();
	}

	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		this.unitsTransferedTo = 0L;
		this.unitsTransferedFrom = 0L;
		for (SaleItem sa : this.getSaleItems()) {
			sa.updateUnits();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
			this.unitsTransferedTo += sa.getUnitsTransferedTo();
			this.unitsTransferedFrom += sa.getUnitsTransferedFrom();
		}
		this.updateStatus();
	}
	
	private void updateStatus() {
		this.status = "PENDING_APPROVAL";
		if(this.approved) {
			this.status = "APPROVED";
		}
		if(this.getUnitsOnStock() > 0) {
			this.status = "PARTIAL_ORDER";
		}
		if(this.getUnitsOnStock() >= this.unitsSold) {
			this.status = "ORDERED";
		}
		if(this.unitsScheduled > 0) {
			this.status = "PARTIAL_SCHEDULE";
		}
		if(this.unitsScheduled >= this.unitsSold) {
			this.status = "SCHEDULED";
		}
		if(this.unitsProduced > 0) {
			this.status = "PARTIAL_PROD";
		}
		if(this.unitsProduced >= this.unitsSold) {
			this.status = "PRODUCED";
		}
		if(this.unitsShipped > 0) {
			this.status = "PARTIAL_SHIPPED";
		}
		if(this.unitsShipped >= this.unitsSold) {
			status = "SHIPPED";
		}
	}

}