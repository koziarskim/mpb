package com.noovitec.mpb.app;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.FilterDto;

@Component
public class FilterConverter implements Converter<String, Pageable> {

	private final ObjectMapper objectMapper;

	public FilterConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Pageable convert(String source) {
		try {
			FilterDto filterDto = objectMapper.readValue(source, FilterDto.class);
			Sort sort = filterDto.isSortDesc() ? Sort.by(filterDto.getSortBy()).descending() : Sort.by(filterDto.getSortBy()).ascending();
			Pageable pageable = PageRequest.of(filterDto.getCurrentPage() - 1, filterDto.getPerPage(), sort);
			return pageable;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
