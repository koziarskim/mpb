package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.SupplierDto;
import com.noovitec.mpb.entity.Supplier;

public interface SupplierRepo extends JpaRepository<Supplier, Long> {

	@Query("select distinct new com.noovitec.mpb.dto.SupplierDto(s.id, s.name, s.account, (ps.id is not null)) "
			+ "from Supplier s " 
			+ "left join s.components c "
			+ "left join c.itemComponents ic "
			+ "left join ic.item i "
			+ "left join i.saleItems si "
			+ "left join si.sale sale "
			+ "left join sale.purchaseSales ps "
			+ "where ps.purchase.id = :purchase_id")
	public List<SupplierDto> findAllSuppliersForPurchase(@Param("purchase_id") Long purchase_id);

}