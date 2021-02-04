package com.noovitec.mpb.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.ComponentAdjustment;
import com.noovitec.mpb.repo.custom.CustomComponentAdjustmentRepo;

public interface ComponentAdjustmentRepo extends PagingAndSortingRepository<ComponentAdjustment, Long>, CustomComponentAdjustmentRepo {
	
	@Query(value = "select ca from ComponentAdjustment ca where ca.component.id = :componentId")
	Collection<ComponentAdjustment> findByComponent(@Param("componentId") Long componentId);


}