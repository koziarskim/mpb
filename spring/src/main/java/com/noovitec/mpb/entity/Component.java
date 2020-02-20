package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
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
	private Long unitsOnStock = 0L;
	private int unitsOrdered = 0; // All Purchases.
	private int unitsInTransit = 0; // All Purchases.
	private int unitsReceived = 0; // All Purchases.
	private Long unitsLocked;

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

	// TODO: Need to persist it.
	@Transient
	private Long unitsInOrder = 0L;

	public Long getUnitsInOrder() {
		Long units = 0L;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			units += (pc.getUnits() - pc.getUnitsReceived());
		}
		return units;
	}

	// Returns extra units.
	public void addUnitsOnStock(Long units) {
		this.unitsOnStock += units;
	}
}