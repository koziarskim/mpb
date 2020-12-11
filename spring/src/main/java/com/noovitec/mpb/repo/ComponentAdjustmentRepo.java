package com.noovitec.mpb.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.ComponentAdjustment;

public interface ComponentAdjustmentRepo extends JpaRepository<ComponentAdjustment, Long> {
	
	@Query(value = "select ca from ComponentAdjustment ca where ca.component.id = :componentId")
	Collection<ComponentAdjustment> findByComponent(@Param("componentId") Long componentId);


}