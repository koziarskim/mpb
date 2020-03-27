package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer extends BaseEntity {

	public enum INVOICE_TYPE {
		PER_FIRST_SHIPMENT, PER_SHIPMENT_SALE, PER_SHIPMENT_ITEM
	}
	
	private String name;
	private String account;
	private String vendor;
	private String phone;
	private String phone2;
	private String paymentTerms;
	private String freightTerms;
	private String email;
	private String email2;
	private String contactName;
	private String shipmentNotes;
	private Long unitsSold;
	private Long unitsShipped;
	private String invoiceType;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "customer_address", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	@OrderBy("id DESC")
	private Collection<Address> addresses = new HashSet<Address>();

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "billig_address_id", referencedColumnName = "id")
	private Address billingAddress;

}