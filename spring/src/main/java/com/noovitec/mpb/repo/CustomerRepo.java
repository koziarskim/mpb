package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
}