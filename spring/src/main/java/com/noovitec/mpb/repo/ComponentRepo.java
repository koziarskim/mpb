package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Component;

public interface ComponentRepo extends JpaRepository<Component, Long> {
	Component findByName(String name);
}