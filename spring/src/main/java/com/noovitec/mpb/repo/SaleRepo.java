package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Sale;

public interface SaleRepo extends JpaRepository<Sale, Long> {

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(s.id, concat(s.number, '-', s.customer.name)) "
			+ "from Sale s "
			+ "where s.produced = false "
			+ "and s.customer.id = :customer_id")
	public List<KeyValueDto> findSalesForCustomer(@Param("customer_id") Long customer_id);

	@Query(value="select s from Sale s where s.customer.id = :customer_id ")
	public List<Sale> findSaleByCustomer(@Param("customer_id") Long sale_id);
	
	@Query("select s from Sale s")
	Page<Sale> findPage(Pageable pageable);

	@Query("select s from Sale s "
			+ "join Customer c on c.id = s.customer.id "
			+ "join Address a on a.id = s.shippingAddress.id "
			+ "where upper(s.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(s.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Sale> findPageBySale(Pageable pageable, String searchKey);

	@Query("select s from Sale s "
			+ "join s.saleItems sa "
			+ "join sa.item i "
			+ "where upper(i.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(i.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Sale> findPageByItem(Pageable pageable, String searchKey);
	

}