package com.noovitec.mpb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class SaleItemTransfer extends BaseEntity {

	private Long unitsTransfered = 0L;
	@Column(name="sale_item_from_id", insertable=false, updatable=false)
	private Long saleItemFromId;
	@Column(name="sale_item_to_id", insertable=false, updatable=false)
	private Long saleItemToId;

	@JsonIgnoreProperties(value = { "transfersTo", "transfersFrom" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_from_id", referencedColumnName = "id")
	private SaleItem saleItemFrom;

	@JsonIgnoreProperties(value = { "transfersTo", "transfersFrom" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_to_id", referencedColumnName = "id")
	private SaleItem saleItemTo;

}