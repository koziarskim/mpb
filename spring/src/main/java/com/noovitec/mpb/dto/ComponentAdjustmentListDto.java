package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentAdjustmentListDto {

	private Long id;
	private long unitsAdjusted;
	private LocalDate date;
	private String reason;
	private String notes;
	private Long componentId;
	private String componentNumber;
	private String componentName;
	
}
