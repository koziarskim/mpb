package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SaleItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private int units; //Units sold.
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private BigDecimal totalUnitPrice = BigDecimal.ZERO;

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
	private String label;
	@Transient
	private Long unitsScheduled = 0L; //This is total units scheduled (including all schedule events). Updated when saving ScheduleEvent (ScheduleEvent.unitsScheduled)
	@Transient
	private Long unitsProduced = 0L; //This should be updated on Production save/post (Production.unitsProduced)
	@Transient
	boolean itemCompleted = false;
	@Transient
	private Long unitsShipped = 0L;
	
	public String getLabel() {
		if(this.getSale()!=null) {
			this.label = this.getSale().getNumber() + " - " + this.getSale().getCustomer().getName();
		}else {
			this.label = "";
		}
		return this.label;
	}
	
	public Long getUnitsScheduled() {
		this.unitsScheduled = 0L;
		for(ScheduleEvent se: this.getScheduleEvents()) {
			this.unitsScheduled += se.getUnitsScheduled();
		}
		return this.unitsScheduled;
	}
	
	public Long getUnitsProduced() {
		this.unitsProduced = 0L;
		for(ScheduleEvent se: this.getScheduleEvents()) {
			for(Production p: se.getProductions()) {
				this.unitsProduced += p.getUnitsProduced();
			}
		}
		return this.unitsProduced;
	}

	@JsonIgnore
	public BigDecimal getAverageProduced() {
		BigDecimal averageProduced = BigDecimal.ZERO;
		if(this.getScheduleEvents().size()==0) {
			return averageProduced;
		}
		BigDecimal avgProduced = BigDecimal.ZERO;
		int count = 0;
		for(ScheduleEvent se: this.getScheduleEvents()) {
			if(se.getAverageProduced().equals(BigDecimal.ZERO)) {
				continue;
			}
			avgProduced = avgProduced.add(se.getAverageProduced());
			count++;
		}
		if(count>0) {
			averageProduced = avgProduced.divide(BigDecimal.valueOf(count),2, RoundingMode.HALF_DOWN);
		}
		return averageProduced;
	}

	public boolean isItemProduced() {
		return this.getUnitsScheduled() <= this.getUnitsProduced();
	}
	
	public Long getUnitsShipped() {
		this.unitsShipped = 0L;
		for(ShipmentItem si: this.getShipmentItems()) {
			this.unitsShipped += si.getUnits();
		}
		return this.unitsShipped;
	}
	
}