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

	private long units = 0;
	private BigDecimal unitPrice = BigDecimal.ZERO;

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

	@Transient
	private Long unitsReceived;

	public Long getUnitsReceived() {
		Long units = 0L;
		for (Receiving r : this.getReceivings()) {
			if (r.getReceivingDate() != null) {
				units += Long.valueOf(r.getUnits());
			}
		}
		return units;
	}

	@Transient
	private Long unitsInTransit = 0L;

	public Long getUnitsInTransit() {
		Long units = 0L;
		for (Receiving r : this.getReceivings()) {
			if (r.getReceivingDate() == null) {
				units += Long.valueOf(r.getUnits());
			}
		}
		return units;
	}

	@Transient
	private boolean received;

	public boolean isReceived() {
		if (this.getUnitsReceived() != 0 && this.getUnitsReceived() >= this.getUnits()) {
			return true;
		}
		return false;
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