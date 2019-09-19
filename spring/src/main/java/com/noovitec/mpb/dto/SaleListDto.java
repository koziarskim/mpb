package com.noovitec.mpb.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleListDto {
	
	private Long id;
	private String number;
	private String customerName;
	private Date date;
	private String dc;
	private Long unitsSold;
	private Long unitsScheduled;
	private Long unitsProduced;
	
	public String getStatus() {
		if(this.unitsSold==0) {
			return "No Sale";
		}
//		System.out.print("Perc: "+Math.round((this.unitsProduced.doubleValue() * 100)/this.unitsSold.doubleValue()));
		//TODO: Fix me!!!!
		return Math.round(this.unitsProduced.doubleValue()/this.unitsSold.doubleValue()*100)+"%";
	}
}
