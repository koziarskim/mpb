package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private String name;
	private String number;
	private String description;
	private int height = 0;
	private int width = 0;
	private int depth = 0;
	private BigDecimal weight = BigDecimal.ZERO;
	private int casePack = 1;
	private int caseHeight = 0;
	private int caseWidth = 0;
	private int caseDepth = 0;
	private BigDecimal caseWeight = BigDecimal.ZERO;
	private int ti = 1; // number of cases in single layer on pallet.
	private int hi = 1; // number of layers on pallet.
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private BigDecimal laborCost = BigDecimal.ZERO;
	private BigDecimal otherCost = BigDecimal.ZERO;
	private BigDecimal totalCost = BigDecimal.ZERO;
	private Long unitsProduced = 0L; //Updated by SaleItemListener.
	private Long unitsSold = 0L; //Updated by SaleItemListener.
	private Long unitsScheduled = 0L; //Updated by SaleItemListener.
	private Long unitsShipped = 0L; //Updated by SaleItemListener.

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "season_id", referencedColumnName = "id")
	private Season season;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "upc_id", referencedColumnName = "id")
	private Upc upc;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "case_upc_id", referencedColumnName = "id")
	private Upc caseUpc;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@Transient
	private String label;

	public String getLabel() {
		return this.getNumber() + " - " + this.getName();
	}

	public Long getDurationSeconds() {
		Long secs = 0L;
		for(SaleItem si: this.getSaleItems()) {
			for(ScheduleEvent se: si.getScheduleEvents()) {
				secs += se.getDurationSeconds();
			}
		}
		return secs;
	}

	@Transient
	private Long unitsOnStock = 0L;
	
	public Long getUnitsOnStock() {
		this.unitsOnStock = this.getUnitsProduced() - this.getUnitsShipped();
		return this.unitsOnStock;
	}
	
	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		for(SaleItem sa: this.getSaleItems()) {
			sa.updateUnits();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsScheduled();
			this.unitsShipped += sa.getUnitsShipped();
		}
	}
	
	public Long getUnitsProduced() {
		this.unitsProduced = 0L;
		for(SaleItem si : this.getSaleItems()) {
			this.unitsProduced += si.getUnitsProduced();
		}
		return this.unitsProduced;
	}
	
	public Long getUnitsScheduled() {
		this.unitsScheduled = 0L;
		for(SaleItem si : this.getSaleItems()) {
			this.unitsScheduled += si.getUnitsScheduled();
		}
		return this.unitsScheduled;
	}
	
	public Long getUnitsSold() {
		this.unitsSold = 0L;
		for(SaleItem si : this.getSaleItems()) {
			this.unitsSold += si.getUnits();
		}
		return this.unitsSold;
	}
	
	public Long getUnitsShipped() {
		this.unitsShipped = 0L;
		for(SaleItem si : this.getSaleItems()) {
			this.unitsShipped += si.getUnitsShipped();
		}
		return this.unitsShipped;
	}
}