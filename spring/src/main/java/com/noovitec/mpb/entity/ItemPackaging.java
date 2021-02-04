package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class ItemPackaging extends BaseEntity {

	private static final long serialVersionUID = -6345762674113542595L;
	private long unitsOnFloor;
	private long unitsOnStock;
	private long salesNotAssigned;
	private long unitsNotAssigned;
	private long unitsShort;
	private long unitsPenShip;
	private long salesOpen;
	private long unitsOpen;
	private long unitsProduced;
	private long unitsAssigned;
	private long unitsScheduled;
	private long unitsSold;
	private long unitsAdjusted;
	private long unitsShipped;
	
	@JsonIgnoreProperties(value = { "itemComponents", "itemPackagings", "saleItems", "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "itemPackagings", "saleItems", "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "packaging_id", referencedColumnName = "id")
	private Packaging packaging;
	
	@JsonIgnoreProperties(value = { "itemPackaging", "saleItem", "item" }, allowSetters = true)
	@OneToMany(mappedBy = "itemPackaging")
	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();

	@JsonIgnoreProperties(value = { "scheduleEvents", "itemPackaging", "item" }, allowSetters = true)
	@OneToMany(mappedBy = "itemPackaging")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@Transient
	private String label;
	
	public String getLabel() {
		return this.getPackaging().getName() + " ("+Packaging.TYPE.valueOf(this.getPackaging().getType()).label()+")";
	}
	
	public void updateUnits() {
		this.unitsOnFloor = 0;
		this.unitsOnStock = 0;
		this.salesNotAssigned = 0;
		this.unitsNotAssigned = 0;
		this.unitsShort = 0;
		this.unitsPenShip = 0;
		this.salesOpen = 0;
		this.unitsOpen = 0;
		this.unitsProduced = 0;
		this.unitsAssigned = 0;
		this.unitsScheduled = 0;
		this.unitsSold = 0;
		this.unitsAdjusted = 0;
		this.unitsShipped = 0;
		this.unitsOnFloor = 0;
		
		for (SaleItem si : this.getSaleItems()) {
			si.updateUnits();
			if(!si.getSale().isCancelled()) {
				this.unitsSold += si.getUnits();
				this.unitsAdjusted += si.getUnitsAdjusted();
				this.unitsNotAssigned += si.getUnitsNotAssigned();
				if(si.getUnitsNotAssigned() != 0) {
					this.salesNotAssigned += 1;
				}
				this.unitsPenShip += (si.getUnits() + si.getUnitsAdjusted()) - si.getUnitsShipped();
				if(!si.getSale().isPaidInFull()) {
					this.salesOpen += 1;
					this.unitsOpen += (si.getUnits() + si.getUnitsAdjusted());
				}
				this.unitsShipped += si.getUnitsShipped();
				this.unitsAssigned += si.getUnitsAssigned();
			}
		}
		for(ScheduleEvent se: this.getScheduleEvents()) {
			se.updateUnits();
			this.unitsProduced += se.getUnitsProduced();
			this.unitsScheduled += se.getUnitsScheduled();
		}
		this.unitsOnFloor = this.unitsProduced - this.unitsShipped;
		this.unitsOnStock = this.unitsProduced - this.unitsAssigned;
		this.unitsShort = (this.unitsSold + this.unitsAdjusted) - this.unitsOnStock - this.unitsAssigned;
	}
}