package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.custom.CustomItemRepo;

public interface ItemRepo extends PagingAndSortingRepository<Item, Long>, CustomItemRepo {
	Item findByName(String name);

	@Query("select distinct i from Item i where i.id in :itemIds")
	List<Item> findByIds(List<Long> itemIds);

	@Query("select distinct i from Item i "
			+ "join i.itemComponents ic "
			+ "where ic.component.id = :componentId")
	List<Item> findByComponent(Long componentId);

	@Query(value = "select i.* from Item i order by i.id desc limit 1", nativeQuery = true)
	Item getLast();
	
	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(i.id, concat(i.number, ' (', i.name, ')')) from Item i")
	public List<KeyValueDto> getAllKeyValueDtos();

}