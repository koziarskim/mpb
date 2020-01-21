package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class Shipment extends BaseEntity {

	private String number;
	private LocalDate shippingDate;
	private LocalDate shippedDate;
	private boolean ready;
	private String notes;
	private String via;
	private String fob;
	private String freightNmfc;
	private String freightTerms;
	private String loadNumber;
	private Long totalUnits = 0L;
	private Long totalCases = 0L;
	private Long totalPallets = 0L;
	private BigDecimal totalWeight = BigDecimal.ZERO;

	@JsonIgnoreProperties(value = { "sales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@JsonIgnoreProperties(value = { "data" }, allowSetters = true)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@ManyToOne()
	@JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
	private Address shippingAddress;

	@ManyToOne()
	@JoinColumn(name = "freight_address_id", referencedColumnName = "id")
	private Address freightAddress;

	@JsonIgnoreProperties(value = { "shipment" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "shipment_id")
	private Collection<ShipmentItem> shipmentItems = new HashSet<ShipmentItem>();

}