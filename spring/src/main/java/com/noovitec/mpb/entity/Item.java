package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	private Long unitsProduced = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsShipped = 0L;
	private Long unitsReadyProd = 0L;
	private Long unitsReturned = 0L;
	private Long unitsOnStock = 0L;

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
			perf = BigDecimal.valueOf(average).divide(BigDecimal.valueOf(schedules),2,RoundingMode.HALF_UP);
		}
		return perf.longValue();
	}

	public void updateUnitsReadyProd() {
		List<Long> units = Stream.of(0L).collect(Collectors.toList());
		for (ItemComponent ic: this.getItemComponents()) {
			Long uts = (ic.getComponent().getUnitsOnStock() - ic.getComponent().getUnitsLocked()) * ic.getUnits().longValue();
			units.add(uts);
		}
		Collections.sort(units);
		this.unitsReadyProd = units.get(0);
	}
	
	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		this.unitsReturned = 0L;
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
		}
		this.unitsOnStock = this.unitsProduced - this.unitsShipped + (this.unitsReturned==null?0L:this.unitsReturned);
//		this.updateUnitsReadyProd();
	}

}