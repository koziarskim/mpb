package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

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
	private String dc;

	@Transient
	private String label;

	public String getLabel() {
		return this.dc + " - " + this.street;
	}
}