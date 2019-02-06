package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;

public interface ItemRepo extends JpaRepository<Item, Long> {
	Item findByName(String name);
}