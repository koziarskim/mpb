package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.ItemReturn;

public interface ItemReturnRepo extends PagingAndSortingRepository<ItemReturn, Long> {
	
	@Query("select ir from ItemReturn ir")
	public List<ItemReturn> findAll();

	@Query("select ir from ItemReturn ir "
			+ "join ir.saleItemReturns sir "
			+ "join sir.saleItem si "
			+ "join si.item i "
			+ "join si.sale s "
			+ "where i.id = :itemId")
	public List<ItemReturn> findByItem(Long itemId);

	@Query("select ir from ItemReturn ir "
			+ "join ir.saleItemReturns sir "
			+ "join sir.saleItem si "
			+ "join si.item i "
			+ "join si.sale s "
			+ "where s.id = :saleId")
	public List<ItemReturn> findBySale(Long saleId);

	@Query("select ir from ItemReturn ir "
			+ "join ir.saleItemReturns sir "
			+ "join sir.saleItem si "
			+ "join si.item i "
			+ "join si.sale s "
			+ "where i.id = :itemId "
			+ "and s.id = :saleId")
	public List<ItemReturn> findByItemAndSale(Long itemId, Long saleId);

}