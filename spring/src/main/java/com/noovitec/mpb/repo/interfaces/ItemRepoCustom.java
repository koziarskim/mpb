package com.noovitec.mpb.repo.interfaces;

import java.util.List;

import com.noovitec.mpb.dto.KeyValueDto;

public interface ItemRepoCustom {

	public List<KeyValueDto> findFiltered(String itemName);

}