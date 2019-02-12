package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

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
	private BigDecimal assumedPrice = BigDecimal.ZERO;
	private BigDecimal dutyFee = BigDecimal.ZERO;
	private BigDecimal deliveryFee = BigDecimal.ZERO;

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
	
	@Transient
	private Boolean locked;
	
	public Boolean getLocked() {
		return (this.itemComponents==null || this.itemComponents.size()==0)?false:true;
	}

}