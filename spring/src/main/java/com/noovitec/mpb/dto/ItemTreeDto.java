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
	private Long unitsOnStock = 0L;
	private Long totalSold = 0L;
	private Long totalProduced = 0L;
	private Long totalSeconds = 0L;
	private Long totalAverage = 0L;
	private Long dailyScheduled = 0L;
	private Long dailyProduced = 0L;
	private Long dailySeconds = 0L;
	private Long dailyAverage = 0L;
	
	List<ScheduleEventTreeDto> events = new ArrayList<ScheduleEventTreeDto>();
	
	public Long getDailyAverage() {
		Long total = 0L;
		Long count = 0L;
		for(ScheduleEventTreeDto event: this.getEvents()) {
			if(event.getDailyAverage()==0L) {
				continue;
			}
			total+=event.getDailyAverage();
			count++;
		}
		if(count==0L) {
			return 0L;
		}
		Long average = BigDecimal.valueOf(total).divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_DOWN).longValue();
		return average;
	}

	public Long getTotalAverage() {
		if(this.getDailySeconds()==0L || this.getEvents().size()==0) {
			return this.dailyAverage;
		}
		BigDecimal average = BigDecimal.valueOf(this.getTotalProduced())
				.divide(BigDecimal.valueOf(this.getTotalSeconds()),2, RoundingMode.HALF_DOWN)
				.multiply(BigDecimal.valueOf(3600));
		this.totalAverage = average.longValue();
		return this.totalAverage;
	}
}
