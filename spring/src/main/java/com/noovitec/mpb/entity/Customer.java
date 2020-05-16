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
//@SecondaryTable(name = "base_customer", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"), schema="shared")
public class Customer extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public enum INVOICE_TYPE {
		PER_SHIPMENT_ITEM, PER_SHIPMENT_SALE, PER_FIRST_SHIPMENT, PER_LAST_SHIPMENT
	}
	
	private String name;
	private String account;
	private String vendor;
	private String paymentTerms;
	private String freightTerms;
	private String shipmentNotes;
	private String invoiceType;
	private String brokerContact;
	private String brokerName;
	private String brokerPhone;
	private String brokerEmail;
	private String shipType;
	
	private String vendorPortal;
	private String vendorGuide;
	private boolean edi;
	private String shipTo;
	
	private boolean priceTicket;
	private String ticketSource;
	private String ticketPosition;
	
	private boolean seasonalCarton;
	private String cartonRequirements;
	private boolean cartonLabel;
	private String labelType;
	private String labelRequirements;
	
	private boolean seasonPalletMark;
	private String palletRequirements;
	private String palletTagSize;
	private String palletTagRequirements;
	
	private boolean routing;
	private boolean routingGuide;
	private boolean asn;
	private boolean claim;
	
	private String routingPortal;
	private String routingCredentials;
	private String routingNotes;
	private String trafficContact;
	
	private String asnPortal;
	private String bolReqirements;
	private boolean carrier;
	private String carrierList;
	private String palletType;
	
	private String claimLink;
	private String claimContact;
	private String claimCredentials;
	private String claimProcess;

	private String portal;
	private String apEmail;
	
	

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