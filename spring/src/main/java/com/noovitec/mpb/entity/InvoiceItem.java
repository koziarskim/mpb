package com.noovitec.mpb.entity;

import java.math.BigDecimal;

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
public class InvoiceItem extends BaseEntity {

    private BigDecimal unitPrice;
    private Long unitsInvoiced;
	private BigDecimal totalUnitPrice;
	
	@JsonIgnoreProperties(value = { "invoiceItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "invoice_id", referencedColumnName = "id")
	private Invoice invoice;

	@JsonIgnoreProperties(value = { "invoiceItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_id", referencedColumnName = "id")
	private SaleItem saleItem;

}