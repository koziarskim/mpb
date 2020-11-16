package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Packaging;

public interface PackagingRepo extends JpaRepository<Packaging, Long> {

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(p.id, concat(p.name)) from Packaging p")
	public List<KeyValueDto> getAllKv();
	
	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(p.id, concat(p.name)) from ItemPackaging ip "
			+ "join ip.item i "
			+ "join ip.packaging p "
			+ "where i.id = :itemId")
	List<KeyValueDto> findKvsByItem(Long itemId);


	
}