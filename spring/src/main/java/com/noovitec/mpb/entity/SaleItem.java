package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SaleItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private int units; //Units sold.
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private BigDecimal totalUnitPrice = BigDecimal.ZERO;

	@JsonIgnoreProperties(value={ "saleItems", "purchaseSales" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "sale_id", referencedColumnName = "id")
	private Sale sale;

	@JsonIgnoreProperties(value={ "saleItems", "itemComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;
	
	@Transient
	private String label;
	
	public String getLabel() {
		if(this.getSale()!=null) {
			this.label = this.getSale().getNumber() + " - " + this.getSale().getCustomer().getName();
		}else {
			this.label = "";
		}
		return this.label;
	}
}