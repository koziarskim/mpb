package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagingListDto {
	
	private Long id;
	private String name;
	private String type;
	
}
