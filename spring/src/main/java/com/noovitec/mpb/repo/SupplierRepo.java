package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Supplier;

public interface SupplierRepo extends PagingAndSortingRepository<Supplier, Long> {

	@Query("select s from Supplier s")
	Page<Supplier> findPage(Pageable pageable);

	@Query("select s from Supplier s where upper(s.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Supplier> findPageBySupplier(Pageable pageable, String searchKey);

	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Supplier s ")
	public List<KeyValueDto> findAllDtos();

}