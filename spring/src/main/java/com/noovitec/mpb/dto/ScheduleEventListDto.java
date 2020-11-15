package com.noovitec.mpb.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEventListDto {

	private Long id;
	private Long saleId;
	private String saleNumber;
	private Long itemId;
	private String itemNumber;
	private String itemName;
	private Long saleItemId;
	private Long lineId;
	private Long packagingId;
	private String packagingLabel;
	private LocalDate scheduleDate;
	private LocalTime startTime;
	private LocalTime finishTime;
	private long unitsSoldAdj;
	private long unitsScheduled;
	private long unitsProduced;
}
