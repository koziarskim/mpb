package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Group;

public interface GroupRepo extends JpaRepository<Group, Long> {
	Group findByName(String name);
}