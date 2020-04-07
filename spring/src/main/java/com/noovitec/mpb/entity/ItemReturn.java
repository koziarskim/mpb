package com.noovitec.mpb.entity;

import java.time.LocalDate;
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
public class ItemReturn extends BaseEntity {

	private long unitsReceived = 0;
	private long unitsReturned = 0;
	private LocalDate dateReturned;

	@JsonIgnoreProperties(value = { "itemReturns" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "itemReturns" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@JsonIgnoreProperties(value = { "itemReturn" }, allowSetters = true)
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "item_return_id")
	private Collection<SaleItemReturn> saleItemReturns = new HashSet<SaleItemReturn>();
	
	public void updateUnits() {
		this.unitsReturned = 0L;
		for(SaleItemReturn sir: this.getSaleItemReturns()) {
			this.unitsReturned += sir.getUnitsReturned();
		}
	}

}