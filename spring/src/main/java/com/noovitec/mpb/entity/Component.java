package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.Column;
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
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Component {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	private String name;
	private String stockNumber;
	private String supplierStockNumber;
	private String description;
	private String picture;
	private BigDecimal assumedPrice;
	private BigDecimal dutyFee;
	private BigDecimal deliveryFee;

	@JsonIgnoreProperties({ "component" })
	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents;
	
	@JsonIgnoreProperties({ "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@JsonIgnoreProperties({ "components" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;
	
	//Transient not managed by DB
	@Transient
	private BigDecimal totalPrice;
	
	public BigDecimal getTotalPrice() {
		BigDecimal totalPrice = new BigDecimal(0);
		totalPrice = this.assumedPrice.add(this.dutyFee).add(this.deliveryFee);
		return totalPrice;
	}

}