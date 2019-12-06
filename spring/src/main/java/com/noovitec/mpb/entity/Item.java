package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

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
	private Long unitsProduced = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsShipped = 0L;

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
	private Long unitsOnStock = 0L;

	public Long getUnitsOnStock() {
		this.unitsOnStock = this.getUnitsProduced() - this.getUnitsShipped();
		return this.unitsOnStock;
	}
	
	@Transient
	private Long performance;
	
	public Long getPerformance() {
		Long average = 0L;
		Long schedules = 0L;
		Long perf = 0L;
		for(SaleItem si: this.getSaleItems()) {
			for(ScheduleEvent se: si.getScheduleEvents()) {
				Long totalTime = se.getTotalTime();
				if(totalTime != null) {
					if(se.getUnitsProduced()>0) {
						average += (totalTime*3600)/se.getUnitsProduced();
					}
					schedules++;
				}
			}
		}
		if(schedules >0) {
			perf = average/schedules;
		}
		return perf;
	}

	public void updateUnits() {
		this.unitsSold = 0L;
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		for (SaleItem sa : this.getSaleItems()) {
			sa.updateUnits();
			this.unitsSold += sa.getUnits();
			this.unitsScheduled += sa.getUnitsScheduled();
			this.unitsProduced += sa.getUnitsProduced();
			this.unitsShipped += sa.getUnitsShipped();
		}
	}

}