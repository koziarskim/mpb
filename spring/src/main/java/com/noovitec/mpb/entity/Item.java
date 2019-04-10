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
	private BigDecimal weight = BigDecimal.ZERO;
	private int casePack = 1;
	private int caseHeight = 0;
	private int caseWidth = 0;
	private int caseDepth = 0;
	private BigDecimal caseWeight = BigDecimal.ZERO;
	private int ti = 1; // number of cases in single layer on pallet.
	private int hi = 1; // number of layers on pallet.
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	private BigDecimal laborCost = BigDecimal.ZERO;
	private BigDecimal otherCost = BigDecimal.ZERO;
	private BigDecimal totalCost = BigDecimal.ZERO;
	private String status = "NONE"; // This is "DYNAMIC" in DB because it is calculated on the GET.

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "season_id", referencedColumnName = "id")
	private Season season;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "upc_id", referencedColumnName = "id")
	private Upc upc;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "case_upc_id", referencedColumnName = "id")
	private Upc caseUpc;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@JsonIgnoreProperties(value = { "item" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();

	@Transient
	private String label;

	public String getLabel() {
		return this.getNumber() + " - " + this.getName();
	}

	public void setStatus(String status) {
		this.status = "DYNAMIC";
	}

	public String getStatus() {
		this.status = this.calculateStatus();
		return this.status;
	}

	public String calculateStatus() {
		Set<String> status = new HashSet<String>();
		for (ItemComponent ic : this.getItemComponents()) {
			Component c = ic.getComponent();
			if (c.getUnitsOnStack() >= ic.getUnits()) {
				status.add("READY");
			} else if (c.getUnitsInTransit() > 0) {
				status.add("TRANSIT");
			} else if (c.getUnitsOrdered() > 0) {
				status.add("ORDERED");
			}
		}
		if (status.contains("READY")) {
			this.status = "READY";
		}
		if (status.contains("TRANSIT")) {
			this.status = "TRANSIT";
		}
		if (status.contains("ORDERED")) {
			this.status = "ORDERED";
		}
		return this.status == null ? "SHORT" : this.status;
	}

}