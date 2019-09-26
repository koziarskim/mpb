package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	private Long dailyScheduled = 0L;
	private Long dailyProduced = 0L;
	private Long dailySeconds = 0L;
	private Long dailyAverage = 0L;
	private BigDecimal averageProduced = BigDecimal.ZERO;
	
	List<ScheduleEventTreeDto> events = new ArrayList<ScheduleEventTreeDto>();
	
	public Long getDailyAverage() {
		if(this.getDailySeconds()==0L || this.getEvents().size()==0) {
			return this.dailyAverage;
		}
		BigDecimal average = BigDecimal.valueOf(this.getDailyProduced())
				.divide(BigDecimal.valueOf(this.getDailySeconds()),2, RoundingMode.HALF_DOWN)
				.multiply(BigDecimal.valueOf(3600));
		this.dailyAverage = average.longValue();
		return this.dailyAverage;
	}
}
