package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.CustomerDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) from Customer c")
	public List<KeyValueDto> findAllCustomers();

	@Query(value = "select new com.noovitec.mpb.dto.CustomerDto(c.id, c.account, c.name, c.phone) from Customer c")
	public List<CustomerDto> findAllDtos();
	
	@Query("select cu from Customer cu")
	Page<Customer> findPage(Pageable pageable);

	@Query("select cu from Customer cu "
			+ "where upper(cu.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Customer> findPageByCustomer(Pageable pageable, String searchKey);


}