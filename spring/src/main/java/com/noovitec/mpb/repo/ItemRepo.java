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
	
	@Query("select i "
			+ "from Item i "
			+ "join i.saleItems si "
			+ "join si.sale s "
			+ "join s.purchaseSales ps "
			+ "left join i.brand b "
			+ "left join i.category c "
			+ "join ps.purchase.purchaseComponents pc "
			+ "where ps.purchase.id = :purchase_id")
	List<Item> getPurchaseItems(@Param("purchase_id") Long purchase_id);

	//Not used
	@Query("select new com.noovitec.mpb.dto.ItemDto"
			+ "(i.id, i.number, i.name, b.name, c.name, i.status) "
			+ "from Item i "
			+ "left join i.brand b "
			+ "left join i.category c")
	List<ItemDto> findAllDtos();

}