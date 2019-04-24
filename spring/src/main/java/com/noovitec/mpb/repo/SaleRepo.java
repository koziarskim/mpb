package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.SaleDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.Sale;

public interface SaleRepo extends JpaRepository<Sale, Long> {

	@Query(value="select new com.noovitec.mpb.dto.SaleDto(s.id, s.number, s.date, c.name, a.dc, (ps.id is not null)) "
			+ "from Sale s "
			+ "left join s.purchaseSales ps with ps.purchase.id = :purchase_id "
			+ "left join s.customer c "
			+ "left join s.shippingAddress a")
	public List<SaleDto> findAllSalesAndPurchaseSales(@Param("purchase_id") Long purchase_id);

	@Query(value="select new com.noovitec.mpb.dto.SaleItemDto(si.id, s.id, s.number, c.name, si.units) "
			+ "from SaleItem si "
			+ "join si.sale s "
			+ "join s.customer c "
			+ "where si.item.id = :item_id")
	public List<SaleItemDto> findAllByItem(@Param("item_id") Long item_id);

}