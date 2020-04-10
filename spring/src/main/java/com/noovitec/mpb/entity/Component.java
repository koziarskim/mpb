package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(indexes = { @Index(name = "IDX_COMPONENT_ID", columnList = "id") })
public class Component extends BaseEntity {

	private String name;
	private String number;
	private String supplierStockNumber;
	private String description;
	private Long unitsPerContainer = 1L;
	private BigDecimal height;
	private BigDecimal width;
	private BigDecimal depth;
	private BigDecimal weight;
	private int casePack = 1;
	private BigDecimal unitCost;
	private BigDecimal dutyPercentage;
	private BigDecimal deliveryCost = BigDecimal.ZERO;
	private BigDecimal containerCost;
	private BigDecimal otherCost;
	private BigDecimal totalLandedCost = BigDecimal.ZERO;
	private long unitsOnStock = 0; //unitsReceived - unitsForProduction
	private long unitsOrdered = 0;
	private long unitsReceived = 0;
	private long unitsLocked = 0; //unitsScheduled - unitsProduced
	private long unitsForProduction = 0; //Produced
	private long unitsForSale = 0; //Sold
	private long unitsSoldNotProd = 0; //unitsForSale - unitsForProduction
	private long unitsShort = 0; //unitsSoldNotProduced - unitsOnStock - unitsOrdered - unitsReceived

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

	public void updateUnitsLocked() {
		this.unitsLocked = 0L;
		for(ItemComponent ic: this.getItemComponents()) {
			this.unitsLocked += (ic.getItem().getUnitsScheduled() - ic.getItem().getUnitsProduced()) * ic.getUnits();
		}
	}
}