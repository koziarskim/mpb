package com.noovitec.mpb.dto;

import java.math.BigDecimal;

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
	private Long unitsScheduled = 0L;
	private Long unitsProduced = 0L;
	private BigDecimal averageProduced = BigDecimal.ZERO;
}
