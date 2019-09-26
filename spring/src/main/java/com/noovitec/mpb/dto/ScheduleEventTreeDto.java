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
	private Long unitsSold = 0L;
	private Long saleTotalProduced = 0L;
	private Long dailyScheduled = 0L;
	private Long dailyProduced = 0L;
	private Long dailyAverage = 0L;
	private Long dailySeconds = 0L;
	
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
