package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Supplier;

public interface SupplierRepo extends JpaRepository<Supplier, Long> {
}