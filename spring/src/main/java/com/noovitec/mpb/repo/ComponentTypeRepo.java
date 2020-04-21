package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.ComponentType;

public interface ComponentTypeRepo extends JpaRepository<ComponentType, Long> {

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(id, name) from ComponentType")
	List<KeyValueDto> findAllKvs();


}