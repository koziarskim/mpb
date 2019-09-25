package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTreeDto {

	private Long id;
	private String name;
	private String status;
	private boolean show;
	private Long unitsOnStack = 0L;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsProduced = 0L;
	private BigDecimal averageProduced = BigDecimal.ZERO;
	
	List<ScheduleEventTreeDto> events = new ArrayList<ScheduleEventTreeDto>();
}
