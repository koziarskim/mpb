package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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
	private BigDecimal purchaseCost = BigDecimal.ZERO;
	private BigDecimal dutyPercentage = BigDecimal.ZERO;
	private BigDecimal deliveryCost = BigDecimal.ZERO;
	private BigDecimal containerCost = BigDecimal.ZERO;
	private int unitsPerContainer;
	private BigDecimal otherCost = BigDecimal.ZERO;
	private BigDecimal height = BigDecimal.ZERO;
	private BigDecimal width = BigDecimal.ZERO;
	private BigDecimal depth = BigDecimal.ZERO;
	private BigDecimal weight = BigDecimal.ZERO;
	private int unitsPerCase;

	@JsonIgnoreProperties({ "component" })
	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "component_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties({ "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@JsonIgnoreProperties({ "items", "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	// Transient not managed by DB

	@Transient
	private BigDecimal totalCost;

	public BigDecimal getTotalCost() {
		BigDecimal totalCost = new BigDecimal(0);
		totalCost = this.purchaseCost.add(this.dutyPercentage).add(this.deliveryCost).add(this.otherCost);
		return totalCost;
	}

	@Transient
	private Boolean locked;

	public Boolean getLocked() {
		return (this.itemComponents == null || this.itemComponents.size() == 0) ? false : true;
	}
	
	@Transient
	private String label;
	
	public String getLabel() {
		return this.getNumber() +" - "+this.getName();
	}

}