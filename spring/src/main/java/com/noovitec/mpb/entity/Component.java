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
	private int number;
	private String supplierStockNumber;
	private String description;
	private BigDecimal assumedPrice = BigDecimal.ZERO;
	private BigDecimal dutyFee = BigDecimal.ZERO;
	private BigDecimal deliveryFee = BigDecimal.ZERO;
	private BigDecimal otherFee = BigDecimal.ZERO;
	private BigDecimal height = BigDecimal.ZERO;
	private BigDecimal width = BigDecimal.ZERO;
	private BigDecimal depth = BigDecimal.ZERO;
	private BigDecimal weight = BigDecimal.ZERO;
	private BigDecimal unitsPerCase = BigDecimal.ZERO;

	@JsonIgnoreProperties({ "component" })
	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "component_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>();

	@JsonIgnoreProperties({ "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@JsonIgnoreProperties({ "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private Category category;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	// Transient not managed by DB

	@Transient
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		BigDecimal totalPrice = new BigDecimal(0);
		totalPrice = this.assumedPrice.add(this.dutyFee).add(this.deliveryFee).add(this.otherFee);
		return totalPrice;
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