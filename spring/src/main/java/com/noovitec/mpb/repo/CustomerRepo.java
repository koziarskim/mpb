package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(c.id, c.name) " + "from Customer c")
	public List<KeyValueDto> findAllCustomers();

}