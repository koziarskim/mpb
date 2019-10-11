package com.noovitec.mpb.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

	private Long id;
	private LocalDate date;
	private List<LineDto> lines = new ArrayList<LineDto>();
}
