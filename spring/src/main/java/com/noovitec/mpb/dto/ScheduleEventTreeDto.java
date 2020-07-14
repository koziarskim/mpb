package com.noovitec.mpb.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEventTreeDto {

	private Long id;
	private String customerName;
	private String saleNumber;
	private String lineNumber;
	private String status;
	private boolean show;
	private long unitsSold = 0;
	private long saleTotalProduced = 0;
	private long dailyScheduled = 0;
	private long dailyProduced = 0;
	private long dailyAverage = 0;
	private long dailySeconds = 0;
	private long dailyPeople = 0;
	
	public Long getDailyAverage() {
		if(this.getDailySeconds()==0L) {
			return this.dailyAverage;
		}
		BigDecimal average = BigDecimal.valueOf(this.getDailyProduced())
				.divide(BigDecimal.valueOf(this.getDailySeconds()),2, RoundingMode.HALF_DOWN)
				.multiply(BigDecimal.valueOf(3600));
		this.dailyAverage = average.longValue();
		return this.dailyAverage;
	}
}
