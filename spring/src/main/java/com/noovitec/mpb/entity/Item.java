package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
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
	private Long unitsOnStack = 0L;
	private Long unitsScheduled = 0L;

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

//	@JsonIgnore
//	@OneToMany()
//	@JoinColumn(name = "item_id")
//	private Collection<ScheduleEvent> scheduleEvents = new HashSet<ScheduleEvent>();

//	Should we remove this property from DB?	
//	@Transient
//	private Long unitsScheduled;
	
	public Long getUnitsScheduled() {
		Long units = 0L;
		for(SaleItem si : this.getSaleItems()) {
			units += si.getUnitsScheduled();
		}
		return units;
	}
	
	@Transient
	private Long unitsProduced;
	
	public Long getUnitsProduced() {
		Long units = 0L;
		for(SaleItem si : this.getSaleItems()) {
			units += si.getUnitsProduced();
		}
		return units;
	}
	
	@Transient
	private String label;

	public String getLabel() {
		return this.getNumber() + " - " + this.getName();
	}

	public void setStatus(String status) {
		this.status = "DYNAMIC";
	}

	public void addUnitsOnStack(Long units) {
		if (this.unitsOnStack == null) {
			this.unitsOnStack = 0L;
		}
		this.unitsOnStack += units;
	}

	public void addUnitsScheduled(Long units) {
		if (this.unitsScheduled == null) {
			this.unitsScheduled = 0L;
		}
		this.unitsScheduled += units;
	}

}