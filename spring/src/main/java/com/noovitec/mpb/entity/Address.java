package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

	@Id
	@GeneratedValue
	private Long id;
	private String street;
	private String city;
	private String state;
	private String zip;
	private boolean defaultFlag = false;

	@JsonIgnoreProperties({ "addresses" })
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	// Transient not managed by JPA
	@Transient
	private String label;

	public String getLabel() {
		return this.street + (this.defaultFlag?"- default":"");
	}
}