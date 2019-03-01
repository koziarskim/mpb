package com.noovitec.mpb.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

	@JsonIgnoreProperties({ "customer" })
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "customer_id")
	@OrderBy("id DESC")
	private Set<Address> addresses = new HashSet<Address>();

//	@JsonIgnoreProperties({ "customer" })
//	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//	private Set<Address> addresses = new HashSet<Address>();

	@JsonIgnoreProperties({ "customer" })
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "customer_id")
	@OrderBy("id DESC")
	private Set<Sale> sales = new HashSet<Sale>();

//	@JsonIgnoreProperties({ "customer" })
//	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//	private Set<Sale> sales = new HashSet<Sale>();

}