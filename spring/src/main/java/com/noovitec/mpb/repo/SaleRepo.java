package com.noovitec.mpb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;

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

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(s.id, concat(s.number, '-', s.customer.name)) "
			+ "from Sale s "
			+ "where s.produced = false "
			+ "and s.customer.id = :customer_id")
	public List<KeyValueDto> findSalesForCustomer(@Param("customer_id") Long customer_id);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(si.id, si.item.number, si.item.name) "
			+ "from SaleItem si "
			+ "where si.sale.id = :sale_id ")
	public List<KeyValueDto> findSaleItemsBySale(@Param("sale_id") Long sale_id);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(si.id, si.sale.number) from SaleItem si ")
	public List<KeyValueDto> findAllKvs();

	@Query(value="select si "
			+ "from SaleItem si "
			+ "where si.id = :sale_item_id ")
	public Optional<SaleItem> getSaleItemById(@Param("sale_item_id") Long sale_item_id);

	@Query(value="select s from Sale s where s.customer.id = :customer_id ")
	public List<Sale> findSaleByCustomer(@Param("customer_id") Long sale_id);
	
	@Query("select s from Sale s")
	Page<Sale> getSalePageable(Pageable pageable);

	@Query("select s from Sale s "
			+ "left join Customer c on c.id = s.customer.id "
			+ "left join Address a on a.id = s.shippingAddress.id "
			+ "where upper(s.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%') ")
	Page<Sale> getSalePageable(Pageable pageable, String searchKey);


}