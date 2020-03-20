package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.repo.custom.CustomSaleRepo;

public interface SaleRepo extends JpaRepository<Sale, Long>, CustomSaleRepo {

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(s.id, concat(s.number, '-', s.customer.name)) "
			+ "from Sale s "
			+ "where s.customer.id = :customer_id")
	public List<KeyValueDto> findKvByCustomer(Long customer_id);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(s.id, concat(s.number, '-', s.customer.name)) "
			+ "from Sale s "
			+ "where s.customer.id = :customer_id "
			+ "and s.unitsOnStock > 0")
	public List<KeyValueDto> findKvByCustomerAndStock(Long customer_id);

	@Query(value="select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, s.number) from Sale s where s.number is not null")
	public List<KeyValueDto> findKvs();

	@Query(value="select s from Sale s where s.customer.id = :customer_id ")
	public List<Sale> findSaleByCustomer(Long customer_id);
	
	@Query("select s from Sale s where s.id in :saleIds")
	List<Sale> findByIds(List<Long> saleIds);

	@Query(value="select s from Sale s "
			+ "join s.saleItems si "
			+ "join si.item i "
			+ "where i.id in :itemIds")
	public List<Sale> findByItems(List<Long> itemIds);

	@Query(value="select s from Sale s "
			+ "join s.saleItems si "
			+ "where si.id = :saleItemId")
	public Sale getBySaleItem(Long saleItemId);

	@Query(value="select s.id from Sale s where s.number = :number")
	public Long getIdByNumber(String number);

}