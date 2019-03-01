package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Sale;

public interface SaleRepo extends JpaRepository<Sale, Long> {
}