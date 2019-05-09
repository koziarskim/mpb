package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueDto {

	private Long id;
	//Deprecated use id instead.
	private Long key;
	private Object value;

	public KeyValueDto(Long id, Object value) {
		this.id = id;
		this.key = id;
		this.value = value;
	}
}
