package com.noovitec.mpb.entity;

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
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String account;
	private String phone;
	private String phone2;
	private String paymentTerms;
	private int freightTerms;
	private String email;
	private String email2;
	private String contactName;

//	@JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id")
	@OrderBy("id DESC")
	private Collection<Address> addresses = new HashSet<Address>();

//	@JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "billig_address_id", referencedColumnName = "id")
	private Address billingAddress;

//	@JsonIgnoreProperties({ "customer" })
//	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//	private Set<Address> addresses = new HashSet<Address>();

	@JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "customer_id")
	@OrderBy("id DESC")
	private Collection<Sale> sales = new HashSet<Sale>();

//	@JsonIgnoreProperties({ "customer" })
//	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//	private Set<Sale> sales = new HashSet<Sale>();

}