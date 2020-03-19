package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c")
	public List<KeyValueDto> findAllCustomers();

	@Query(value = "select distinct new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c "
			+ "join Sale s on s.customer.id = c.id "
			+ "join s.saleItems si "
			+ "where si.item.id = :itemId")
	public List<KeyValueDto> findByItem(Long itemId);

	@Query("select cu from Customer cu")
	Page<Customer> findPage(Pageable pageable);

	@Query("select cu from Customer cu "
			+ "where upper(cu.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Customer> findPageByCustomer(Pageable pageable, String searchKey);
	
	@Query("select cu from Customer cu where cu.id in :customerIds")
	List<Customer> findByIds(List<Long> customerIds);
	
	@Query("select sum(s.unitsSold) from Sale s "
			+ "join s.customer cu "
			+ "where cu.id = :customerId")
	Long getUnitsSold(Long customerId);

	@Query("select sum(s.unitsShipped) from Sale s "
			+ "join s.customer cu "
			+ "where cu.id = :customerId")
	Long getUnitsShipped(Long customerId);

}