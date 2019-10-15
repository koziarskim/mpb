package com.noovitec.mpb.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {

	Set<Long> seasons = new HashSet<Long>();
	String seasonName = "";
}
