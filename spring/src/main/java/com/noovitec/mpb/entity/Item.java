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
	private int height = 0;
	private int width = 0;
	private int depth = 0;
	private int weight = 0;
	private int unitsPerCase = 1;
	private int caseHeight = 0;
	private int caseWidth = 0;
	private int caseDepth = 0;
	private int caseWeight = 0;
	private int ti = 1; //number of cases in single layer on pallet.
	private int hi = 1; //number of layers on pallet.
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
	
	@JsonIgnoreProperties({ "items" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "case_upc_id", referencedColumnName = "id")
	private Upc caseUpc;

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