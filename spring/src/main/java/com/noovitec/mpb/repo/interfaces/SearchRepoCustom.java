package com.noovitec.mpb.repo.interfaces;

import java.util.List;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SearchDto;

public interface SearchRepoCustom {

	public List<KeyValueDto> findSeasons(SearchDto searchDto);

	public List<KeyValueDto> findCustomers(String customerName);
	
	public List<KeyValueDto> findItems(String itemName, Long supplierId);

	public List<KeyValueDto> findSales(String saleNumber);

	public List<KeyValueDto> findSuppliers(String supplierName);

	public List<KeyValueDto> findComponents(String componentName);

}