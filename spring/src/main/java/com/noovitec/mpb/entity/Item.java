package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	private BigDecimal totalCost;
	private long unitsProduced = 0;
	private long unitsSold = 0;
	private long unitsOpenSale = 0;
	private long unitsScheduled = 0;
	private long unitsShipped = 0;
	private long unitsReadyProd = 0;
	private long unitsReturned = 0;
	private long unitsOnStock = 0;
	private long unitsAdjusted = 0;

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();
	
	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "item_id")
	private Collection<ItemPackaging> itemPackagings = new HashSet<ItemPackaging>();

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

	@JsonIgnore
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();

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
		this.unitsOpenSale = 0;
		this.unitsScheduled = 0;
		this.unitsProduced = 0;
		this.unitsShipped = 0;
		this.unitsReturned = 0;
		this.unitsAdjusted = 0;
		this.unitsOnStock = 0;
		for(ItemReturn ir: this.getItemReturns()) {
			ir.updateUnits();
		}
		for (SaleItem sa : this.getSaleItems()) {
			sa.updateUnits();
			this.unitsReturned += sa.getUnitsReturned();
			if(!sa.getSale().isCancelled()) {
				this.unitsSold += sa.getUnits();
			}
			if(!sa.getSale().isCancelled() && !sa.getSale().isPaidInFull()) {
				this.unitsOpenSale += 1;
			}
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
			this.unitsAdjusted += sa.getUnitsAdjusted();
			this.unitsOnStock += sa.getUnitsOnStock();
		}
		this.updateUnitsReadyProd();
	}
}