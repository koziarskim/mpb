package com.noovitec.mpb.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Receiving {

	@Id
	@GeneratedValue
	private Long id;
	private String number;
	private String reference;
	private int units;
	private Date shippedDate = new Date();
	private Date etaDate = new Date();
	private Date receivedDate = new Date();

	@JsonIgnoreProperties(value = { "receivings" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchaseComponent_id", referencedColumnName = "id")
	private PurchaseComponent purchaseComponent;

	public String getNumber() {
		if(this.number == null) {
			this.number = this.getId().toString();
		}
		return this.number;
	}
}