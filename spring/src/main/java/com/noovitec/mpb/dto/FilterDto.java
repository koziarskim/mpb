package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDto {

	private String sortBy;
	private boolean sortDesc;
	private int perPage;
	private int currentPage;
}
