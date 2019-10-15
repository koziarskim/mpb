package com.noovitec.mpb.repo.interfaces;

import java.util.List;

import com.noovitec.mpb.dto.KeyValueDto;

public interface SearchRepoCustom {

	public List<KeyValueDto> findSuppliers(String supplierName);
	
	public List<KeyValueDto> findItems(String itemName, Long supplierId);

	public List<KeyValueDto> findSales(String saleNumber);

}