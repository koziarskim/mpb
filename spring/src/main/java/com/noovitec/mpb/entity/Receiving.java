package com.noovitec.mpb.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Receiving extends BaseEntity {

	private String number;
	private String name;
	private String containerNumber;
	private String invoiceNumber;
	private Long units;
	private LocalDate shippingDate;
	private LocalDate etaDate;
	private LocalDate receivingDate;

	@JsonIgnoreProperties(value = { "receivings" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchaseComponent_id", referencedColumnName = "id")
	private PurchaseComponent purchaseComponent;

}