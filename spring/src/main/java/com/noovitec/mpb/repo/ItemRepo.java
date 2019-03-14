package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.ItemDto;
import com.noovitec.mpb.entity.Item;

public interface ItemRepo extends JpaRepository<Item, Long> {
	Item findByName(String name);

	@Query(value = "select i.* from Item i order by i.id desc limit 1", nativeQuery = true)
	Item getLast();
	
	@Query("select distinct new com.noovitec.mpb.dto.ItemDto"
			+ "(i.id, i.number, i.name, '', '') "
			+ "from Item i "
			+ "join i.itemComponents ic "
			+ "join ic.component.purchaseComponents pc "
			+ "where pc.purchase.id = :purchase_id")
	List<ItemDto> getPurchaseItems(@Param("purchase_id") Long purchase_id);


}