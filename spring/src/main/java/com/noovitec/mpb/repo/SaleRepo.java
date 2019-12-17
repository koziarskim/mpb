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
	public List<KeyValueDto> findSalesForCustomer(@Param("customer_id") Long customer_id);

	@Query(value="select distinct new com.noovitec.mpb.dto.KeyValueDto(s.id, concat(s.number, '-', s.customer.name)) from Sale s")
	public List<KeyValueDto> findKvs();

	@Query(value="select s from Sale s where s.customer.id = :customer_id ")
	public List<Sale> findSaleByCustomer(@Param("customer_id") Long sale_id);
	
}