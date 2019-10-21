package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
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
public class PurchaseComponent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private Long units = 0L;
	private BigDecimal unitPrice = BigDecimal.ZERO;

	@JsonIgnoreProperties(value = { "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;

	@JsonIgnoreProperties(value = { "itemComponents", "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;

	@JsonIgnoreProperties(value = { "purchaseComponent" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "purchaseComponent_id")
	private Collection<Receiving> receivings = new HashSet<Receiving>();

	@Transient
	private Long unitsReceived;
	
	public Long getUnitsReceived() {
		Long units = 0L;
		for(Receiving r: this.getReceivings()) {
			if(r.getReceivingDate()!=null) {
				units += Long.valueOf(r.getUnits());
			}
		}
		return units;
	}
	
	@Transient
	private Long unitsInTransit = 0L;
	
	public Long getUnitsInTransit() {
		Long units = 0L;
		for(Receiving r: this.getReceivings()) {
			if(r.getReceivingDate()==null) {
				units += Long.valueOf(r.getUnits());
			}
		}
		return units;
	}
			
	@Transient
	private boolean received;
	
	public boolean isReceived() {
		if (this.getUnitsReceived()!=0 && this.getUnitsReceived() >= (this.getUnits()==null?0:this.getUnits())) {
			return true;
		}
		return false;
	}
	
	@Transient
	private String componentNumber;
	
	public String getComponentNumber() {
		return this.getComponent()!=null?this.getComponent().getNumber():null;
	}
	
	public BigDecimal getTotalPrice() {
		BigDecimal unitPrice = this.getUnitPrice()==null?BigDecimal.ZERO:this.getUnitPrice();
		BigDecimal units = this.getUnits()==null?BigDecimal.ZERO:BigDecimal.valueOf(this.getUnits()); 
		return unitPrice.multiply(units);
	}
}