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
	
	@JsonIgnoreProperties({"item"})
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private Collection<ItemComponent> itemComponents = new HashSet<ItemComponent>(); 
	
	@JsonIgnoreProperties({ "items" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	private Brand brand;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	//Transient not managed by DB
	@Transient
	private BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		BigDecimal totalPrice = new BigDecimal(0);
		for(ItemComponent ic : this.itemComponents) {
			totalPrice.add(ic.getComponent().getTotalPrice()
					.multiply(BigDecimal.valueOf(ic.getUnits())));
		}
		return totalPrice;
	}

}