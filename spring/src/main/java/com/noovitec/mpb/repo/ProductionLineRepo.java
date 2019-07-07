package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.ProductionLine;

public interface ProductionLineRepo extends JpaRepository<ProductionLine, Long> {

}