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
public class Sale {

	@Id
	@GeneratedValue
	private Long id;
	private Date date;
	private String number;
	private String paymentTerms;
	private String freightTerms;
	private Date expectedDate;

	@JsonIgnoreProperties(value={ "sales" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

//	@JsonIgnoreProperties(value = { "customer" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address shippingAddress;

	@JsonIgnoreProperties(value={ "sale" }, allowSetters=true)
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "sale_id")
	private Collection<SaleItem> saleItems = new HashSet<SaleItem>();
	
	@JsonIgnoreProperties(value={ "sale" }, allowSetters=true)
	@OneToMany()
	@JoinColumn(name = "sale_id")
	private Collection<PurchaseSale> purchaseSales = new HashSet<PurchaseSale>();

}