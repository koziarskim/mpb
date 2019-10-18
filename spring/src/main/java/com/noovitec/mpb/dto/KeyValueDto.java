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
	private String name;

	public KeyValueDto(Long id, Object value) {
		this.id = id;
		this.key = id;
		this.value = value;
		if(value instanceof String) {
			this.name = value.toString();
		}
	}

	public KeyValueDto(Long id, Object value, String name) {
		this.id = id;
		this.key = id;
		this.value = value;
		this.name = name;
	}

}
