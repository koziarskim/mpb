package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
	@GeneratedValue
	private Long id;
	private int units;

	@JsonIgnoreProperties(value={ "purchaseComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;

	@JsonIgnoreProperties(value={ "itemComponents", "purchaseComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;
	
	@Transient
	private int receivedUnits;
	@Transient
	private boolean received;
	
	public int getReceivedUnits() {
		int units = 0;
		for(Receiving r : this.getComponent().getReceivings()) {
			units += r.getUnits();
		}
		return units;
	}
	
	public boolean isReceived() {
		if(this.getReceivedUnits() >= this.getUnits()) {
			return true;
		}
		return false;
	}
	
}