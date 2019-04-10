package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jdk.internal.jline.internal.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PurchaseComponent {

	@Id
	@GeneratedValue
	private Long id;
	private Long units; //Units ordered;
	private Long unitsOrdered = 0L; //Units submitted.
	private Long unitsReceived = 0L;
	private Long unitsInTransit = 0L;

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
	private boolean received;
	@Transient
	private String componentNumber;

	public void updateUnits() {
		Long unitsInTransit = 0L;
		Long unitsReceived = 0L;
		for (Receiving r : this.getReceivings()) {
			if(r.getReceivedDate()!=null) {
				unitsReceived += r.getUnits();
			}else if(r.getShippedDate()!=null) {
				unitsInTransit += r.getUnits();
			}
		}
		this.unitsOrdered = 0L;
		if(this.getPurchase()!=null && this.getPurchase().isSubmitted()) {
			this.unitsOrdered = this.getUnits();			
		}
		this.unitsReceived = unitsReceived;
		this.unitsInTransit = unitsInTransit;
	}

	public boolean isReceived() {
		if (this.getUnitsReceived() >= (this.getUnits()==null?0:this.getUnits())) {
			return true;
		}
		return false;
	}
	
	public String getStatus() {
		if(this.isReceived()) {
			return "Received";
		}
		if(this.getPurchase().isSubmitted()) {
			if(this.getUnitsInTransit()!=null && this.getUnitsInTransit()>0) {
				return "In Transit";
			}
			return "Ordered";
		}
		return "Pending";
	}
	
	public String getComponentNumber() {
		return this.getComponent()!=null?this.getComponent().getNumber():null;
	}
}