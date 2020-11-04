package com.noovitec.mpb.entity;

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
public class ItemPackaging extends BaseEntity {

	private static final long serialVersionUID = -6345762674113542595L;
	private long unitsOnStock;
	
	@JsonIgnoreProperties(value = { "itemPackagings", "saleItems", "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "itemPackagings", "saleItems", "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "packaging_id", referencedColumnName = "id")
	private Packaging packaging;
	
	@Transient
	private String label;
	
	public String getLabel() {
		return this.getPackaging().getName() + " ("+Packaging.TYPE.valueOf(this.getPackaging().getType()).label()+")";
	}
	
}