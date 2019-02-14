package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.Item;

public interface ItemRepo extends JpaRepository<Item, Long> {
	Item findByName(String name);

	@Query(value = "select i.* from Item i order by i.id desc limit 1", nativeQuery = true)
	Item getLast();

}