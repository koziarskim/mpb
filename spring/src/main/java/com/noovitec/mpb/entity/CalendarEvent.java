package com.noovitec.mpb.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table()
public class CalendarEvent extends BaseEntity {
	
	public static enum TYPE {
		SHIPMENT("mpb-default-event"), 
		DELIVERY("mpb-default-event");
		private String style;
		public String style() { 
	        return this.style; 
	    }
	    private TYPE(String style) { 
	        this.style = style; 
	    } 
	}

	private static final long serialVersionUID = -132754886694556898L;
	private String type;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private String heading1;
	private String heading2;
	
	private String title;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	@Transient
	private String klass;
}