package com.noovitec.mpb.repo.interfaces;

import java.util.List;

import com.noovitec.mpb.dto.KeyValueDto;

public interface SearchRepoCustom {

	public List<KeyValueDto> findFiltered(String itemName, Long supplierId);

}