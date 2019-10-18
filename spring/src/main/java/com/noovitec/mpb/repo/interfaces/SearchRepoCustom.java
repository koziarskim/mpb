package com.noovitec.mpb.repo.interfaces;

import java.util.List;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.PoComponentDto;
import com.noovitec.mpb.dto.SearchDto;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.SaleItem;

public interface SearchRepoCustom {

	public List<KeyValueDto> findSeasons(SearchDto searchDto);

	public List<KeyValueDto> findCustomers(SearchDto searchDto);
	
	public List<KeyValueDto> findItems(SearchDto searchDto);

	public List<KeyValueDto> findSales(SearchDto searchDto);

	public List<KeyValueDto> findSuppliers(SearchDto searchDto);

	public List<KeyValueDto> findComponents(SearchDto searchDto);
	
	public List<PoComponentDto> findPoComponents(SearchDto searchDto);

}