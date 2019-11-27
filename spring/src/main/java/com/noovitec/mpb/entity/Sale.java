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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sale extends BaseEntity{

	private LocalDate date;
	private String number;
	private String name;
	private String paymentTerms;
	private String freightTerms;
	private LocalDate expectedDate;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private boolean produced;
	private Long unitsProduced = 0L; //Updated by SaleItemListener.
	private Long unitsSold = 0L; //Updated by SaleItemListener.
	private Long unitsScheduled = 0L; //Updated by SaleItemListener;
	private Long unitsShipped = 0L; //Updated by SaleItemListener;
	
	@JsonIgnoreProperties(value={ "sales" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shippingAddress;

	@JsonIgnoreProperties(value={ "sale" }, allowSetters=true)
	@OneToMany(cascade=CascadeType.MERGE)
	@JoinColumn(name = "sale_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();
	
	
	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		for(SaleItem sa: this.getSaleItems()) {
			sa.updateUnits();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsProduced();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
		}
	}
	
//	public Long getUnitsProduced() {
//		this.unitsProduced = 0L;
//		for(SaleItem si : this.getSaleItems()) {
//			this.unitsProduced += si.getUnitsProduced();
//		}
//		return this.unitsProduced;
//	}
//	
//	public Long getUnitsScheduled() {
//		this.unitsScheduled = 0L;
//		for(SaleItem si : this.getSaleItems()) {
//			this.unitsScheduled += si.getUnitsScheduled();
//		}
//		return this.unitsScheduled;
//	}
//	
//	public Long getUnitsSold() {
//		this.unitsSold = 0L;
//		for(SaleItem si : this.getSaleItems()) {
//			this.unitsSold += si.getUnits();
//		}
//		return this.unitsSold;
//	}
//	
//	public Long getUnitsShipped() {
//		this.unitsShipped = 0L;
//		for(SaleItem si : this.getSaleItems()) {
//			this.unitsShipped += si.getUnitsShipped();
//		}
//		return this.unitsShipped;
//	}
}