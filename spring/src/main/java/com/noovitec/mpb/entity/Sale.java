package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private LocalDate date;
	private String number;
	private String name;
	private String paymentTerms;
	private String freightTerms;
	private LocalDate expectedDate;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private boolean produced;

	@JsonIgnoreProperties(value={ "sales" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shippingAddress;

	@JsonIgnoreProperties(value={ "sale" }, allowSetters=true)
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "sale_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();
	
	@Transient
	private Long unitsSold;
	
	public Long getUnitsSold() {
		this.unitsSold = 0L;
		for(SaleItem si: this.getSaleItems()) {
			this.unitsSold += si.getUnits();
		}
		return this.unitsSold;
	}
	
	@Transient
	private Long unitsScheduled;
	
	public Long getUnitsScheduled() {
		this.unitsScheduled = 0L;
		for(SaleItem si: this.getSaleItems()) {
			this.unitsScheduled += si.getUnitsScheduled();
		}
		return this.unitsScheduled;
	}

	@Transient
	private Long unitsProduced;
	
	public Long getUnitsProduced() {
		this.unitsProduced = 0L;
		for(SaleItem si: this.getSaleItems()) {
			this.unitsProduced += si.getUnitsProduced();
		}
		return this.unitsProduced;
	}
}