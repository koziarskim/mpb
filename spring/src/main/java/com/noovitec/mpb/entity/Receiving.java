package com.noovitec.mpb.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Receiving extends BaseEntity {

	private String number;
	private String name;
	private String containerNumber;
	private String invoiceNumber;
	private long units = 0;
	private LocalDate shippingDate;
	private LocalDate etaDate;
	private LocalDate receivingDate;

	@JsonIgnoreProperties(value = { "receivings" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchaseComponent_id", referencedColumnName = "id")
	private PurchaseComponent purchaseComponent;

	@Transient private Long preUnits;

	public Long getPreUnits() {
		if(this.preUnits == null) {
			this.preUnits = this.units;
		}
		return this.preUnits;
	}

}