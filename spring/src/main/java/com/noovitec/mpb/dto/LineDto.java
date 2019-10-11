package com.noovitec.mpb.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineDto {

	private int number;
	private List<EventDto> events = new ArrayList<EventDto>();
	
}
