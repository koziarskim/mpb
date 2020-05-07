package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class Item extends BaseEntity {

	private String name;
	private String number;
	private String description;
	private BigDecimal height;
	private BigDecimal width;
	private BigDecimal depth;
	private BigDecimal weight;
	private int casePack = 1;
	private BigDecimal caseHeight;
	private BigDecimal caseWidth;
	private BigDecimal caseDepth;
	private BigDecimal caseWeight;
	private BigDecimal palletWeight;
	private int ti = 1; // number of cases in single layer on pallet.
	private int hi = 1; // number of layers on pallet.
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private BigDecimal laborCost;
	private BigDecimal otherCost;
	private BigDecimal totalCost;
	private long unitsProduced = 0;
	private long unitsSold = 0;
	private long unitsScheduled = 0;
	private long unitsShipped = 0;
	private long unitsReadyProd = 0;
	private long unitsReturned = 0;
	private long unitsOnStock = 0;
	private long unitsAdjusted = 0;
	private long unitsOverstock = 0;

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "season_id", referencedColumnName = "id")
	private Season season;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "year_id", referencedColumnName = "id")
	private Year year;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "upc_id", referencedColumnName = "id")
	private Upc upc;

//	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "case_upc_id", referencedColumnName = "id")
//	private Upc caseUpc;

	@ManyToOne()
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@JsonIgnoreProperties(value = { "item", "saleItemReturns" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<ItemReturn> itemReturns = new HashSet<ItemReturn>();

	public Long getDurationSeconds() {
		Long secs = 0L;
		for (SaleItem si : this.getSaleItems()) {
			for (ScheduleEvent se : si.getScheduleEvents()) {
				secs += se.getDurationSeconds();
			}
		}
		return secs;
	}

	@Transient
	private Long unitsReceived = 0L;
	
	public Long getUnitsReceived() {
		Long units = 0L;
		for(ItemReturn ir: this.getItemReturns()) {
			units += ir.getUnitsReceived();
		}
		return units;
	}
	
	@Transient
	private Long performance;
	
	public Long getPerformance() {
		Long average = 0L;
		Long schedules = 0L;
		BigDecimal perf = BigDecimal.ZERO;
		for(SaleItem si: this.getSaleItems()) {
			for(ScheduleEvent se: si.getScheduleEvents()) {
				if(se.getFinishTime()!=null) {
					average += se.getPerformance();
					schedules++;
				}
			}
		}
		if(schedules >0) {
			perf = BigDecimal.valueOf(average).divide(BigDecimal.valueOf(schedules),2,RoundingMode.CEILING);
		}
		return perf.longValue();
	}

	public void updateUnitsReadyProd() {
		this.unitsReadyProd = 1000000000;
		if(this.getItemComponents().size()==0) {
			this.unitsReadyProd = 0;
			return;
		}
		for (ItemComponent ic: this.getItemComponents()) {
			long unitsLocked = this.getUnitsScheduled()-this.getUnitsProduced();
			if(unitsLocked < 0) {
				unitsLocked = 0;
			}
			long units = BigDecimal.valueOf(ic.getComponent().getUnitsOnStock() - unitsLocked).divide(ic.getUnits(),0, RoundingMode.CEILING).longValue();
			if(units < this.unitsReadyProd) {
				this.unitsReadyProd = units<0?0:units;
			}
		}
	}
	
	public void updateUnits() {
		this.unitsSold = 0;
		this.unitsScheduled = 0;
		this.unitsProduced = 0;
		this.unitsShipped = 0;
		this.unitsReturned = 0;
		this.unitsAdjusted = 0;
		this.unitsOnStock = 0;
		this.unitsOverstock = 0;
		for(ItemReturn ir: this.getItemReturns()) {
			ir.updateUnits();
		}
		for (SaleItem sa : this.getSaleItems()) {
			sa.updateUnits();
			this.unitsReturned += sa.getUnitsReturned();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
			this.unitsAdjusted += sa.getUnitsAdjusted();
			this.unitsOnStock += sa.getUnitsOnStock();
			this.unitsOverstock += sa.getUnitsOverstock();
		}
//		this.unitsOnStock = this.unitsProduced + this.unitsReturned - this.unitsShipped;
//		if(this.unitsOnStock < 0) {
//			this.unitsOnStock = 0;
//		}
//		this.unitsOverstock = this.unitsProduced + this.unitsReturned - (this.unitsSold + this.unitsAdjusted);
//		if(this.unitsOverstock < 0) {
//			this.unitsOverstock = 0;
//		}
		this.updateUnitsReadyProd();
	}
}