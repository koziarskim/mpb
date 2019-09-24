package com.noovitec.mpb.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineTreeDto {

	private Long id;
	private String name;
	private String status;
	private boolean show;
	private Long unitsSold = 0L;
	private Long unitsScheduled = 0L;
	private Long unitsProduced = 0L;
	List<ItemTreeDto> items = new ArrayList<ItemTreeDto>();
}
