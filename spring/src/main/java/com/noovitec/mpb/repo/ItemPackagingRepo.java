package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.ItemPackaging;

public interface ItemPackagingRepo extends JpaRepository<ItemPackaging, Long> {

	@Query("select distinct ip from ItemPackaging ip "
			+ "join ip.item i "
			+ "where i.id = :itemId")
	List<ItemPackaging> findAllByItem(Long itemId);

	
}