package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.Component;

public interface ComponentRepo extends JpaRepository<Component, Long> {
	Component findByName(String name);

	@Query(value="select c.* from Component c order by c.id desc limit 1", nativeQuery=true)
	Component getLast();
}
