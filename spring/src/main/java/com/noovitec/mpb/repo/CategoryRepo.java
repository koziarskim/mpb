package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

	@Query("select c from Category c where type = :type")
	public List<Category> findAllByType(String type);

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(id, name) from Category where type = 'ITM'")
	List<KeyValueDto> getAllForItemKeyValue();

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(id, name) from Category where type = 'CMP'")
	List<KeyValueDto> getAllForComponentKeyValue();

}