package com.noovitec.mpb.entity;

import java.math.BigDecimal;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Component {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String number;
	private String supplierStockNumber;
	private String description;
	private int unitsPerContainer = 1;
	private int height = 0;
	private int width = 0;
	private int depth = 0;
	private BigDecimal weight = BigDecimal.ZERO;
	private int casePack = 1;
	private BigDecimal unitCost = BigDecimal.ZERO;
	private BigDecimal dutyPercentage = BigDecimal.ZERO;
	private BigDecimal deliveryCost = BigDecimal.ZERO;
	private BigDecimal containerCost = BigDecimal.ZERO;
	private BigDecimal otherCost = BigDecimal.ZERO;
	private BigDecimal totalLandedCost = BigDecimal.ZERO;
	private int unitsOnStack = 0;
	private Long unitsReserved = 0L;
	private int unitsOrdered = 0; //All Purchases.
	private int unitsInTransit = 0; //All Purchases.
	private int unitsReceived = 0; //All Purchases.

	@JsonIgnoreProperties(value = { "component" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "component_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties(value = { "components" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@JsonIgnoreProperties(value = { "items", "components" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value = { "component" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "component_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();

	// Transient not managed by DB

	@Transient
	private boolean locked;
	@Transient
	private String label;

	public boolean isLocked() {
		return this.itemComponents.size() > 0;
	}

	public String getLabel() {
		return this.getNumber() + " - " + this.getName();
	}

//	//This is for all purchases.
//	public int getUnitsOrdered() {
//		int unitsOrdered = 0;
//		for (PurchaseComponent pc : this.getPurchaseComponents()) {
//			unitsOrdered += pc.getUnitsOrdered();
//		}
//		return unitsOrdered;
//	}
//	
//	//This is for all purchases.
//	public int getUnitsInTransit() {
//		int unitsInTransit = 0;
//		for (PurchaseComponent pc : this.getPurchaseComponents()) {
//			unitsInTransit += pc.getUnitsInTransit();
//		}
//		return unitsInTransit;
//	}
//
//	//This is for all purchases.
//	public int getUnitsReceived(String purchase_id) {
//		int unitsReceived = 0;
//		for(PurchaseComponent pc : this.getPurchaseComponents()) {
//			unitsReceived += pc.getUnitsReceived();
//		}
//		return unitsReceived;
//	}

	//Helper methods
	public void addUnitsOnStack(Long units) {
		this.unitsOnStack += units;
	}

	public void addUnitsReserved(Long units) {
		if(this.unitsReserved==null) {
			this.unitsReserved=0L;
		}
		this.unitsReserved += units;
	}

	public void updateUnits() {
		int unitsOrdered = 0;
		int unitsInTransit = 0;
		int unitsReceived = 0;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			unitsOrdered += ((pc.getUnitsOrdered()==null?0:pc.getUnitsOrdered())-pc.getUnitsReceived()-pc.getUnitsInTransit());
			//Set to 0 if negative.
			unitsOrdered = unitsOrdered < 0?0:unitsOrdered;
			unitsInTransit += (pc.getUnitsInTransit()==null?0:pc.getUnitsInTransit());
			unitsReceived += (pc.getUnitsReceived()==null?0:pc.getUnitsReceived());
		}
		this.unitsOrdered = unitsOrdered;
		this.unitsInTransit = unitsInTransit;
		this.unitsReceived = unitsReceived;
	}
}