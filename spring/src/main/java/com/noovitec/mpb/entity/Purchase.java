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
	private Long unitsReceived;
	
	public Long getUnitsReceived() {
		Long units = 0L;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			units += pc.getUnitsReceived();
		}
		return units;
	}

	@Transient
	private Long unitsPurchased;
	
	public Long getUnitsPurchased() {
		Long units = 0L;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			units += pc.getUnits();
		}
		return units;
	}

	@Transient
	private boolean received;

	public boolean isReceived() {
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			if (!pc.isReceived()) {
				return false;
			}
		}
		return true;
	}

	public BigDecimal getTotalPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (PurchaseComponent pc : this.getPurchaseComponents()) {
			price = price.add(pc.getTotalPrice());
		}
		return price;
	}

}