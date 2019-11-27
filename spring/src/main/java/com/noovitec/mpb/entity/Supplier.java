package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Supplier extends BaseEntity {

	private String name;
	private String account;

	// TODO: Switch to defaultAddress
	private String street;
	private String city;
	private String state;
	private String zip;

	private String phone;
	private String phone2;
	private String paymentTerms;
	private String freightTerms;
	private String email;
	private String email2;
	private String contactName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address address;

	@JsonIgnoreProperties(value = { "supplier" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "supplier_id")
	private Collection<Component> components = new HashSet<Component>();

}