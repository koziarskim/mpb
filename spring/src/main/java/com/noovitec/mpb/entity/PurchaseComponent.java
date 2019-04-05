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
	private int units; //Units ordered;

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

//	@Transient
	private Long unitsReceived;
	@Transient
	private boolean received;
	@Transient
	private String componentNumber;

	public void updateUnitsReceived() {
		Long units = 0L;
		for (Receiving r : this.getReceivings()) {
			units += r.getUnits();
		}
		this.unitsReceived = units;
	}

	public boolean isReceived() {
		if (this.getUnitsReceived() >= this.getUnits()) {
			return true;
		}
		return false;
	}
	
	public String getComponentNumber() {
		return this.getComponent()!=null?this.getComponent().getNumber():null;
	}
}