package com.noovitec.mpb.entity;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification extends BaseEntity {
	
	public enum TYPE {
		SHIPPING_READY, SHIPPING_SHIPPED, CUSTOMER_SALES_SHIPPED, INVOICE_CREATED
	}

	private String type;
	private String entity;
	private Long entityId;
	private String emails;

}