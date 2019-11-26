package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

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

//@EntityListeners(SaleItemListener.class)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SaleItem extends BaseEntity{

	private int units; //unitsSold.
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private BigDecimal totalUnitPrice = BigDecimal.ZERO;
	private Long unitsProduced = 0L; //Updated by ScheduleEventListener.
	private Long unitsScheduled = 0L; //Updated by ScheduleEventListener.
	private Long unitsShipped = 0L; //Updated by ShipmentItemListener.
	
	@JsonIgnoreProperties(value={ "saleItems", "purchaseSales" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "sale_id", referencedColumnName = "id")
	private Sale sale;

	@JsonIgnoreProperties(value={ "saleItems", "itemComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;
	
	@JsonIgnoreProperties(value={ "saleItem"}, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();
	
	@JsonIgnoreProperties(value = { "shipment" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "sale_item_id")
	private Collection<ShipmentItem> shipmentItems = new HashSet<ShipmentItem>();
	
	@Transient
	private Long prevUnits = 0L;
	
	public Long getPrevUnits() {
		this.prevUnits = Long.valueOf(this.units);
		return this.prevUnits;
	}
	
	public Long unitsUpdated() {
		return Long.valueOf(this.units) - this.prevUnits;
	}
	
	@Transient
	private String label;
	
	public String getLabel() {
		if(this.getSale()!=null) {
			this.label = this.getSale().getNumber() + " - " + this.getSale().getCustomer().getName();
		}else {
			this.label = "";
		}
		return this.label;
	}

	@Transient
	private Long unitsOnStock = 0L;
	
	public Long getUnitsOnStock() {
		return this.getUnitsProduced() - this.getUnitsShipped();
	}
	
	public void updateUnits() {
		this.unitsScheduled = 0L;
		this.unitsProduced = 0L;
		this.unitsShipped = 0L;
		for(ScheduleEvent se : this.getScheduleEvents()) {
			se.updateUnits();
			this.unitsScheduled += se.getUnitsScheduled();
			this.unitsProduced += se.getUnitsProduced();
		}
		for(ShipmentItem si: this.getShipmentItems()) {
			this.unitsShipped += si.getUnits();
		}
	}
	
	public Long getUnitsProduced() {
		this.unitsProduced = 0L;
		for(ScheduleEvent se : this.getScheduleEvents()) {
			this.unitsProduced += se.getUnitsProduced();
		}
		return this.unitsProduced;
	}
	
	public Long getUnitsScheduled() {
		this.unitsScheduled = 0L;
		for(ScheduleEvent se : this.getScheduleEvents()) {
			this.unitsScheduled += se.getUnitsScheduled();
		}
		return this.unitsScheduled;
	}
	
	public Long getUnitsSchipped() {
		this.unitsShipped = 0L;
		for(ShipmentItem si : this.getShipmentItems()) {
			this.unitsShipped += si.getUnits();
		}
		return this.unitsShipped;
	}
	
}