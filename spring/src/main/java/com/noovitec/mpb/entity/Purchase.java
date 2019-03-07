package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.Date;
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
	private String paymentTerms;
	private int freightTerms;
	private Date expectedDate;

//	@JsonIgnoreProperties({ "addresses", "purchases" })
	@ManyToOne()
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	private Supplier supplier;

	@JsonIgnoreProperties(value={ "customer" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shipAddress;

	@JsonIgnoreProperties(value={ "purchase" }, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "purchase_id")
	private Collection<PurchaseSale> purchaseSales = new HashSet<PurchaseSale>();

	@JsonIgnoreProperties(value={ "purchase" }, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "purchase_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();
	
}