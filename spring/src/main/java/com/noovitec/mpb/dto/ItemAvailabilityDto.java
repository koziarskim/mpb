package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemAvailabilityDto {

	private Long id;
	private Long unitsScheduled;
	private Long unitsToSchedule;
	private Long unitsToProduction;
}
