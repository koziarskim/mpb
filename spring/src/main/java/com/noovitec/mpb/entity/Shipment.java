package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

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

	private static final long serialVersionUID = -8138964487526041657L;
	private String number;
	private String name;
	private LocalDate shippingDate;
	private LocalDate shippedDate;
	private LocalDateTime modifiedDate;
	private LocalDate shippingFrom;
	private LocalDate shippingTo;
	private LocalTime shippingTime;
	private boolean ready;
	private String notes;
	private String comments;
	private String via;
	private String fob;
	private String freightNmfc;
	private String freightTerms;
	private String freightClass;
	private String loadNumber;
	private long unitsShipped = 0;
	private long totalCases = 0;
	private long totalPallets = 0;
	private long totalPalletsCustom = 0;
	private BigDecimal totalWeight = BigDecimal.ZERO;
	private BigDecimal totalWeightCustom = BigDecimal.ZERO;
	private BigDecimal estimatedCost = BigDecimal.ZERO;
	private BigDecimal invoicedCost = BigDecimal.ZERO;
	private String status;

	@JsonIgnoreProperties(value = { "sales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "shipment_attachment", joinColumns = @JoinColumn(name = "shipment_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	@OrderBy("created DESC")
	private Collection<Attachment> attachments = new HashSet<Attachment>();

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