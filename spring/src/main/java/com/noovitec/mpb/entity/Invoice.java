package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class Invoice extends BaseEntity {

	private String number;
	private LocalDate date;
	private String paymentTerms;
	private LocalDate shippingDate;
	private String via;
	private String fob;
	private String loadNumber;
    private String type;
	private boolean sent;
	private BigDecimal shippingCost;
	private BigDecimal balanceDue;
	private String invoiceEmail;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "shipment_id", referencedColumnName = "id")
	private Shipment shipment;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "billing_address_id", referencedColumnName = "id")
	private Address billingAddress;

	@JsonIgnoreProperties(value = { "items" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
	private Address shippingAddress;
	
	@JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "invoice_id")
	private Collection<InvoiceItem> invoiceItems = new HashSet<InvoiceItem>();

	public Collection<InvoiceItem> getInvoiceItems(){
		if(this.invoiceItems==null) {
			this.invoiceItems = new ArrayList<InvoiceItem>();
		}
		return this.invoiceItems;
	}
}