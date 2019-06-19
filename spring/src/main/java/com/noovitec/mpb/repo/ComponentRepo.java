package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.ComponentDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Component;

public interface ComponentRepo extends PagingAndSortingRepository<Component, Long> {

	@Query("select c from Component c where upper(c.name) LIKE CONCAT('%',UPPER(:nameSearch),'%') or upper(c.supplier.name) LIKE CONCAT('%',UPPER(:nameSearch),'%')")
	Page<Component> findAll(Pageable pageable, String nameSearch);

	Component findByName(String name);

	// TODO: Change it to HQL. Remove limit.
	@Query(value = "select c.* from Component c order by c.id desc limit 1", nativeQuery = true)
	Component getLast();

	@Query("select distinct new com.noovitec.mpb.dto.ComponentDto("
			+ "c.id, c.number, c.name, sum((ic.units * si.units)-c.unitsOnStack-c.unitsOrdered-c.unitsInTransit), c.unitsOnStack, pc.units, pc.unitsReceived, pc.unitsInTransit, c.totalLandedCost, (pc.id is not null)) "
			+ "from Component c " + "join c.itemComponents ic " + "join ic.item i " + "join i.saleItems si " + "join si.sale s "
			+ "join s.purchaseSales ps with ps.purchase.id = :purchase_id "
			+ "left join c.purchaseComponents pc with pc.purchase.id = ps.purchase.id and pc.component.id = c.id " + "where c.supplier.id = :supplier_id "
			+ "group by c.id, pc.id")
	List<ComponentDto> getComponentsForPurchaseAndSupplier(@Param("purchase_id") Long purchase_id, @Param("supplier_id") Long supplier_id);

	@Query("select distinct new com.noovitec.mpb.dto.ComponentDto(" + "c.id, c.number) " + "from Component c " + "order by c.number asc")
	List<ComponentDto> getAllDto();

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(id, concat(number, '-', name)) from Component")
	List<KeyValueDto> getAllKeyValue();

}
