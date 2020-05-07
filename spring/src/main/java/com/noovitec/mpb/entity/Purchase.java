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
import javax.persistence.Transient;

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
public class Purchase extends BaseEntity {

	private static final long serialVersionUID = 1865476568296951741L;
	private LocalDate date;
	private String number;
	private String name;
	private LocalDate expectedDate;
	private LocalDate shippingDate;
	private LocalDate receivingDate;
	private String invoiceNumber;
	private String containerNumber;
	private long unitsReceived = 0;
	private long unitsPurchased = 0;

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

	public void updateUnits() {
		this.unitsPurchased = 0;
		this.unitsReceived = 0;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			this.unitsPurchased += pc.getUnits();
		}
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			this.unitsReceived += pc.getUnitsReceived();
		}
	}
	
	public BigDecimal getTotalPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			price = price.add(pc.getTotalPrice());
		}
		return price;
	}

}