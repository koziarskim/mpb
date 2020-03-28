package com.noovitec.mpb.entity;

import java.util.HashMap;
import java.util.Map;

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
	
	public static enum TYPE {
		SHIPPING_READY("mail/shipmentReady.vm"), SHIPPING_SHIPPED("mail/shipmentShipped.vm"), CUSTOMER_SALES_SHIPPED("mail/customerShipped.vm"), INVOICE_CREATED("mail/invoiceCreated.vm");
		private String template;
		public String template() { 
	        return this.template; 
	    }
	    private TYPE(String template) { 
	        this.template = template; 
	    } 
	}
	public static Map<Enum, String> TEMPLATE = new HashMap<Enum, String>();

	private String type;
	private String entity;
	private Long entityId;
	private String emails;

}