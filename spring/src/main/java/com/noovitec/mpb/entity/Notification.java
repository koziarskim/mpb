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
	
	private static final long serialVersionUID = -7247964667375378237L;
	public static enum TYPE {
		SHIPPING_READY("mail/shipmentReady.vm"), 
		SHIPPING_SHIPPED("mail/shipmentShipped.vm"), 
		INVOICE_CREATED("mail/invoiceCreated.vm"), 
		CUSTOMER_SHIPPED("mail/customerShipped.vm"),
		SALE_READY("mail/saleReady.vm"),
		PENDING_SHIPMENT("mail/pendingShipment.vm"),
		CUSTOMER_CREATED("mail/customerCreated.vm"),
		SCHEDULE_CREATED("mail/scheduleCreated.vm"),
		COMPONENT_RECEIVED("mail/componentReceived.vm"),
		PRODUCTION_COMPLETED("mail/productionCompleted.vm"),
		INVOICE_EMAIL("mail/invoiceEmail.vm");
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