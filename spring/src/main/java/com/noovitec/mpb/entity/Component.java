package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(indexes = { @Index(name = "IDX_COMPONENT_ID", columnList = "id") })
public class Component {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
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
	private Long unitsOnStock = 0L;
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

	@Transient
	private Long unitsInOrder = 0L;
	
	public Long getUnitsInOrder() {
		Long units = 0L;
		for(PurchaseComponent pc: this.getPurchaseComponents()) {
			units += (pc.getUnits() - pc.getUnitsReceived());
		}
		return units;
	}
	
	//Helper methods
	//Returns extra units.
	public void addUnitsOnStock(Long units) {
		this.unitsOnStock += units;
	}
}