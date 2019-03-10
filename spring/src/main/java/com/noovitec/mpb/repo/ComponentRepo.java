package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.ComponentDto;
import com.noovitec.mpb.entity.Component;

public interface ComponentRepo extends JpaRepository<Component, Long> {
	Component findByName(String name);

	//TODO: Change it to HQL. Remove limit.
	@Query(value="select c.* from Component c order by c.id desc limit 1", nativeQuery=true)
	Component getLast();

	@Query("select new com.noovitec.mpb.dto.ComponentDto(c.id, c.number, c.name, i.number, i.name, s.number, s.date, (pc.id is not null)) "
			+ "from Component c " 
			+ "left join c.itemComponents ic "
			+ "left join ic.item i "
			+ "left join i.saleItems si "
			+ "left join si.sale s "
			+ "left join s.purchaseSales ps "
			+ "left join ps.purchase p "
			+ "left join p.purchaseComponents pc "
			+ "where p.id = :purchase_id "
			+ "and c.supplier.id = :supplier_id")
	List<ComponentDto> getComponentsForPurchaseAndSupplier(@Param("purchase_id") Long purchase_id, @Param("supplier_id") Long supplier_id);

}
