package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDto {
	
	private Long id;
	private String heading1;
	private String heading2;
	
	private String title;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	private String start;
	private String end;
	private String klass = "mpb-default-event";
	
}
