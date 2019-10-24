package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Supplier;

public interface SupplierRepo extends PagingAndSortingRepository<Supplier, Long> {

	@Query("select s from Supplier s")
	Page<Supplier> findPage(Pageable pageable);

	@Query("select s from Supplier s where upper(s.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Supplier> findPageBySupplier(Pageable pageable, String searchKey);

	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) "
			+ "from Supplier s " 
			+ "join s.components c "
			+ "join c.itemComponents ic "
			+ "join ic.item i "
			+ "where i.id = :item_id")
	public List<KeyValueDto> findSuppliersByItem(@Param("item_id") Long item_id);

	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.name) from Supplier s ")
	public List<KeyValueDto> findAllDtos();

}