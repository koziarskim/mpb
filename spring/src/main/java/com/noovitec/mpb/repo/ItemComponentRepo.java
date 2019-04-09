package com.noovitec.mpb.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.ItemComponent;

public interface ItemComponentRepo extends JpaRepository<ItemComponent, Long> {

	@Query(value = "select ic from ItemComponent ic where ic.item.id = :item_id")
	Collection<ItemComponent> findByItem(@Param("item_id") Long item_id);
}