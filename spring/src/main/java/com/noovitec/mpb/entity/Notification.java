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
	
	public static enum TYPE {
		SHIPPING_READY("mail/shipmentReady.vm"), SHIPPING_SHIPPED("mail/shipmentShipped.vm"), SALE_SHIPPED("mail/saleShipped.vm"), CUSTOMER_SHIPPED("mail/customerShipped.vm");
		private String template;
		public String template() { 
	        return this.template; 
	    }
	    private TYPE(String template) { 
	        this.template = template; 
	    } 
	}

	private String type;
	private String entity;
	private Long entityId;
	private String emails;

}