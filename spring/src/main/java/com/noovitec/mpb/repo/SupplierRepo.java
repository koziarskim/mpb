package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.SupplierDto;
import com.noovitec.mpb.entity.Supplier;

public interface SupplierRepo extends PagingAndSortingRepository<Supplier, Long> {

	@Query("select s from Supplier s where upper(s.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Supplier> findAll(Pageable pageable, String searchKey);

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