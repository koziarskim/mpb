package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purchase {

	@Id
	@GeneratedValue
	private Long id;
	private Date date;
	private String number;
	private Date expectedDate;
	private boolean completed;
	private BigDecimal totalPrice = BigDecimal.ZERO;

	@JsonIgnoreProperties({ "components" })
	@ManyToOne()
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	private Supplier supplier;

	@JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shipAddress;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;
	
	@JsonIgnoreProperties(value = { "purchase" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "purchase_id")
	private Collection<PurchaseSale> purchaseSales = new HashSet<PurchaseSale>();

	@JsonIgnoreProperties(value = { "purchase" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "purchase_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();
	
	@JsonIgnoreProperties(value = { "purchase" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "purchase_id")
	private Collection<Receiving> receivings = new HashSet<Receiving>();

}