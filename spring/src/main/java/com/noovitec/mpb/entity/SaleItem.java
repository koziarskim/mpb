package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import com.noovitec.mpb.trigger.SaleItemListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@EntityListeners(SaleItemListener.class)
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
	
}