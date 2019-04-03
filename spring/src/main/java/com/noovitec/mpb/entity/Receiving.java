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
	private Date date;

	@JsonIgnoreProperties(value = { "receivings", "supplier", "shipAddress", "attachment", "purchaseSales", "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;

	@JsonIgnoreProperties(value = { "receivings", "itemComponents", "supplier", "category", "attachment", "purchaseComponents" }, allowSetters = true)
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;

}