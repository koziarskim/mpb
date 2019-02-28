package com.noovitec.mpb.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Supplier {

	@Id
	@GeneratedValue
	private Long id;
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
	private int freightTerms;
	private String email;
	private String email2;
	private String contactName;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address address;
}