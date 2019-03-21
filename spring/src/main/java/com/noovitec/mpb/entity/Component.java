package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
	private int unitsOnStack = 0; //Inventory.
	
	@JsonIgnoreProperties(value = { "component" }, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "component_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties(value={ "components" }, allowSetters=true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@JsonIgnoreProperties(value={ "items", "components" }, allowSetters=true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value={ "component" }, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "component_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();

	// Transient not managed by DB

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