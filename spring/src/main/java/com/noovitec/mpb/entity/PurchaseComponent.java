package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

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
public class PurchaseComponent extends BaseEntity {

	private static final long serialVersionUID = 5426750019869118874L;
	private long units = 0;
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private long unitsReceived = 0;

	@JsonIgnoreProperties(value = { "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchase_id", referencedColumnName = "id")
	private Purchase purchase;

	@JsonIgnoreProperties(value = { "itemComponents", "purchaseComponents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;

	@JsonIgnoreProperties(value = { "purchaseComponent" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "purchaseComponent_id")
	private Collection<Receiving> receivings = new HashSet<Receiving>();

	public void updateUnits() {
		this.unitsReceived = 0;
		for (Receiving r : this.getReceivings()) {
			if (r.getReceivingDate() != null) {
				this.unitsReceived += r.getUnits();
			}
		}
	}

	@Transient
	private String componentNumber;

	public String getComponentNumber() {
		return this.getComponent().getNumber();
	}

	public BigDecimal getTotalPrice() {
		BigDecimal unitPrice = this.getUnitPrice() == null ? BigDecimal.ZERO : this.getUnitPrice();
		BigDecimal units = BigDecimal.valueOf(this.getUnits());
		return unitPrice.multiply(units);
	}
}