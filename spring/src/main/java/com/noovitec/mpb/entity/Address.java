package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address extends BaseEntity {

	private static final long serialVersionUID = -6663301163019664754L;
	private String line;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String dc;
	// FRG - Freight
	private String type;
	private boolean visible;
	private String notes;
	private String phone;
	private String locationName;
	
	@Transient
	private String label;

	public String getLabel() {
		return this.dc + " (" + this.city + ", " + this.state+")";
	}
}