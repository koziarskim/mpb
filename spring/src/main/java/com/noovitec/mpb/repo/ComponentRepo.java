package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Component;

public interface ComponentRepo extends PagingAndSortingRepository<Component, Long> {

	@Query("select c from Component c where upper(c.name) LIKE CONCAT('%',UPPER(:nameSearch),'%') "
			+ "or upper(c.number) LIKE CONCAT('%',UPPER(:nameSearch),'%') "
			+ "or upper(c.supplier.name) LIKE CONCAT('%',UPPER(:nameSearch),'%')")
	Page<Component> findAll(Pageable pageable, String nameSearch);

	Component findByName(String name);

	// TODO: Change it to HQL. Remove limit.
	@Query(value = "select c.* from Component c order by c.id desc limit 1", nativeQuery = true)
	Component getLast();

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(c.id, concat(c.number, '-', c.name)) from Component c where c.number is not null")
	List<KeyValueDto> getAllKeyValue();

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(c.id, concat(c.number, '-', c.name)) from Component c "
			+ "join c.purchaseComponents pc "
			+ "where c.number is not null "
			+ "and pc.purchase.id = (:purchase_id)")
	List<KeyValueDto> findKvByPurchase(@Param("purchase_id") Long purchase_id);

}
