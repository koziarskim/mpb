package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.noovitec.mpb.entity.Customer;


public interface CustomerRepository extends CrudRepository<Customer, Long> {
	List<Customer> findByAge(int age);
}
