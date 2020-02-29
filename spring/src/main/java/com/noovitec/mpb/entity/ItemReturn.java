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

	private Long unitsReceived = 0L;
	private Long unitsReturned = 0L;
	private LocalDate dateReturned;

	@JsonIgnoreProperties(value = { "itemReturns" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "itemReturn" }, allowSetters = true)
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "item_return_id")
	private Collection<SaleItemReturn> saleItemReturns = new HashSet<SaleItemReturn>();

}