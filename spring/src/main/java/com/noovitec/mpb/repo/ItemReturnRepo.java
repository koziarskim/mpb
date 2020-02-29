package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.ItemReturn;

public interface ItemReturnRepo extends PagingAndSortingRepository<ItemReturn, Long> {
	
	@Query("select ir from ItemReturn ir")
	public List<ItemReturn> findAll();

}