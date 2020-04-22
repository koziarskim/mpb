package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Brand;

public interface BrandRepo extends JpaRepository<Brand, Long> {
	
	@Query("select new com.noovitec.mpb.dto.KeyValueDto(id, name) from Brand")
	List<KeyValueDto> findAllKvs();
}