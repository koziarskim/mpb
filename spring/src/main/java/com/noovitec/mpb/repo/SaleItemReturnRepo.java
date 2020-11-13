package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.SaleItemReturn;

public interface SaleItemReturnRepo extends PagingAndSortingRepository<SaleItemReturn, Long> {
	
	@Query("select sir from SaleItemReturn sir")
	public List<SaleItemReturn> findAll();

	@Query("select sir from SaleItemReturn sir "
			+ "join sir.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "join si.sale s "
			+ "where i.id = :itemId")
	public List<SaleItemReturn> findByItem(Long itemId);

	@Query("select sir from SaleItemReturn sir "
			+ "join sir.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "join si.sale s "
			+ "where s.id = :saleId")
	public List<SaleItemReturn> findBySale(Long saleId);

	@Query("select sir from SaleItemReturn sir "
			+ "join sir.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "join si.sale s "
			+ "where i.id = :itemId "
			+ "and s.id = :saleId")
	public List<SaleItemReturn> findByItemAndSale(Long itemId, Long saleId);

}