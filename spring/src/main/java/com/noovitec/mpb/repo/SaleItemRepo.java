package com.noovitec.mpb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.custom.CustomSaleItemRepo;

public interface SaleItemRepo extends PagingAndSortingRepository<SaleItem, Long>, CustomSaleItemRepo {

	@Query("select si from SaleItem si where si.id in (:ids)")
	Page<SaleItem> findPage(Pageable pageable, List<Long> ids);

	@Query(value="select si "
			+ "from SaleItem si "
			+ "where si.id = :sale_item_id ")
	public Optional<SaleItem> getSaleItemById(@Param("sale_item_id") Long sale_item_id);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(si.id, si.item.number, si.item.name) "
			+ "from SaleItem si "
			+ "where si.sale.id = :sale_id ")
	public List<KeyValueDto> findSaleItemsBySale(@Param("sale_id") Long sale_id);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(s.number, ' (', i.number, ' - ', i.name, ')')) "
			+ "from SaleItem si "
			+ "join si.sale s "
			+ "join s.customer cu "
			+ "join si.item i "
			+ "where cu.id = :customerId "
			+ "and si.units > si.unitsShipped")
	public List<KeyValueDto> findKvByCustomer(@Param("customerId") Long customerId);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(si.id, si.sale.number) from SaleItem si ")
	public List<KeyValueDto> findAllKvs();
	
	@Query("select si from SaleItem si where si.id in (:ids)")
	public List<SaleItem> findAllByIds(Long[] ids);
	
	
}