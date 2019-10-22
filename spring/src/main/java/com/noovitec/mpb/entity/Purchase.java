package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private LocalDate date;
	private String number;
	private String name;
	private LocalDate expectedDate;
	private LocalDate shippingDate;
	private LocalDate receivingDate;
	private String invoiceNumber;
	private String containerNumber;

	@JsonIgnoreProperties({ "components" })
	@ManyToOne()
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	private Supplier supplier;

	@JsonIgnoreProperties(value = { "data" }, allowSetters = true)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;
	
	@JsonIgnoreProperties(value = { "purchase" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "purchase_id")
	private Collection<PurchaseComponent> purchaseComponents = new HashSet<PurchaseComponent>();
	
	@Transient
	private boolean received;
	
	public boolean isReceived() {
		for(PurchaseComponent pc : this.getPurchaseComponents()) {
			if(!pc.isReceived()) {
				return false;
			}
		}
		return true;
	}
	
	public BigDecimal getTotalPrice() {
		BigDecimal price = BigDecimal.ZERO;
			for(PurchaseComponent pc: this.getPurchaseComponents()) {
				price = price.add(pc.getTotalPrice());
			}
		return price;
	}

}