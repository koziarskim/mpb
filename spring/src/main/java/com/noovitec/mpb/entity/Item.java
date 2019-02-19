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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String number;
	private String description;
	private BigDecimal height = BigDecimal.ZERO;
	private BigDecimal width = BigDecimal.ZERO;
	private BigDecimal depth = BigDecimal.ZERO;
	private BigDecimal weight = BigDecimal.ZERO;
	private BigDecimal unitsPerCase = BigDecimal.ZERO;
	private BigDecimal caseUpc = BigDecimal.ZERO;
	private BigDecimal caseHeight = BigDecimal.ZERO;
	private BigDecimal caseWidth = BigDecimal.ZERO;
	private BigDecimal caseDepth = BigDecimal.ZERO;
	private BigDecimal caseWeight = BigDecimal.ZERO;
	private BigDecimal ti = BigDecimal.ZERO; //number of cases in single layer on pallet.
	private BigDecimal hi = BigDecimal.ZERO; //number of layers on pallet.
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private BigDecimal laborCost = BigDecimal.ZERO;
	private BigDecimal otherCost = BigDecimal.ZERO;

	
	
	@JsonIgnoreProperties({"item"})
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>(); 
	
	@JsonIgnoreProperties({ "items", "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@JsonIgnoreProperties({ "items" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;

	@JsonIgnoreProperties({ "items" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "season_id", referencedColumnName = "id")
	private Season season;
	
	@JsonIgnoreProperties({ "items" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "upc_id", referencedColumnName = "id")
	private Upc upc;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	//Transient not managed by DB
	@Transient
	private BigDecimal totalCost;

	public BigDecimal getTotalCost() {
		BigDecimal totalCost = new BigDecimal(0);
		for(ItemComponent ic : this.itemComponents) {
			totalCost.add(ic.getComponent().getTotalCost()
					.multiply(BigDecimal.valueOf(ic.getUnits())));
		}
		totalCost = totalCost.add(this.warehouseCost).add(this.packageCost).add(this.laborCost).add(this.otherCost);
		return totalCost;
	}

}