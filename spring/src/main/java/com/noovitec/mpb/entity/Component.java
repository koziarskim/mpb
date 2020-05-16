package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

	private static final long serialVersionUID = 5004094587899883068L;
	
	private String name;
	private String number;
	private String supplierStockNumber;
	private String description;
	private long caseWeight;
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
	private BigDecimal averagePrice =BigDecimal.ZERO;
	private String shelf;
	private LocalDate expiration;

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

	@JsonIgnoreProperties(value = { "items", "components" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "component_type_id", referencedColumnName = "id")
	private ComponentType componentType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value = { "component" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "component_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();

	public void updateUnitsLocked() {
		BigDecimal units = BigDecimal.ZERO;
		for(ItemComponent ic: this.getItemComponents()) {
			units = units.add(new BigDecimal(ic.getItem().getUnitsScheduled() - ic.getItem().getUnitsProduced())).multiply(ic.getUnits());
		}
		this.unitsLocked = units.setScale(0, RoundingMode.CEILING).longValue();
		this.unitsLocked = this.unitsLocked<0?0:this.unitsLocked;
	}
}