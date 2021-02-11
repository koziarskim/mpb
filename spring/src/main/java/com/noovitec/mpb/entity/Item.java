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

	private static final long serialVersionUID = -1613201559035575793L;
	private String name;
	private String number;
	private String description;
	private BigDecimal height;
	private BigDecimal length;
	private BigDecimal width;
	private BigDecimal weight;
	private BigDecimal totalCost;
	private long unitsProduced = 0;
	private long unitsSold = 0;
	private long unitsScheduled = 0;
	private long unitsShipped = 0;
	private long unitsOnStock = 0;
	private long unitsAdjusted = 0;
	private long salesNotAssigned = 0;
	private long unitsNotAssigned = 0;
	private long unitsShort = 0;
	private long unitsOnFloor = 0;
	
	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();
	
	@JsonIgnoreProperties(value = { "item", "scheduleEvents", "saleItems" }, allowSetters = true)
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
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

	public Long getDurationSeconds() {
		Long secs = 0L;
		for(ItemPackaging ip: this.getItemPackagings()) {
			for (SaleItem si : ip.getSaleItems()) {
				for (ScheduleEvent se : si.getScheduleEvents()) {
					secs += se.getDurationSeconds();
				}
			}
		}
		return secs;
	}

	public void updateUnits() {
		this.unitsSold = 0;
		this.unitsAdjusted = 0;
		this.salesNotAssigned = 0;
		this.unitsNotAssigned = 0;		
		this.unitsScheduled = 0;
		this.unitsProduced = 0;
		this.unitsShipped = 0;
		this.unitsOnStock = 0;
		this.unitsOnFloor = 0;
		this.unitsShort = 0;
		for(ItemPackaging ip: this.getItemPackagings()) {
			ip.updateUnits();
			this.unitsSold += ip.getUnitsSold();
			this.unitsAdjusted += ip.getUnitsAdjusted();
			this.salesNotAssigned += ip.getSalesNotAssigned();
			this.unitsNotAssigned += ip.getUnitsNotAssigned();
			this.unitsScheduled += ip.getUnitsScheduled();
			this.unitsProduced += ip.getUnitsProduced();
			this.unitsShipped += ip.getUnitsShipped();
			this.unitsOnStock += ip.getUnitsOnStock();
			this.unitsOnFloor += ip.getUnitsOnFloor();
			this.unitsShort += ip.getUnitsShort();
		}
	}
}