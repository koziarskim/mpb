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
		PER_SHIPMENT_ITEM, PER_SHIPMENT_SALE, PER_FIRST_SHIPMENT, PER_LAST_SHIPMENT
	}
	
	private String name;
	private String account;
	private String vendor;
	private String phone;
	private String paymentTerms;
	private String freightTerms;
	private String email;
	private String apEmail;
	private String contactName;
	private String shipmentNotes;
	private long unitsSold = 0;
	private long unitsShipped = 0;
	private String invoiceType;
	private String brokerContact;
	private String brokerName;
	private String brokerPhone;
	private String shipType;
	
	private String vendorPortal;
	private String vendorGuide;
	
	private String priceWebsite;
	private String labelType;
	private String palletTagRequirements;
	private String palletTagSize;
	private boolean edi;
	private boolean priceTicket;
	private boolean cartonLabel;
	private boolean seasonalCarton;
	private boolean seasonPalletMark;
	
	private String routingProcess;
	private String trafficDepartment;
	private boolean routing;
	private boolean delivery;
	private boolean asn;
	private boolean claim;
	private String routingPortal;
	private String routingGuide;
	private String bolPackaging;
	private String asnProcess;
	private String claimProcess;
	private String compliancePortal;

	private String portal;
	
	

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "customer_address", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	@OrderBy("id DESC")
	private Collection<Address> addresses = new HashSet<Address>();

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "billig_address_id", referencedColumnName = "id")
	private Address billingAddress;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "customer_attachment", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	@OrderBy("created DESC")
	private Collection<Attachment> attachments = new HashSet<Attachment>();

}