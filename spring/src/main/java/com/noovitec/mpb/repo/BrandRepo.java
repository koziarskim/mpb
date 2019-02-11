package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Brand;

public interface BrandRepo extends JpaRepository<Brand, Long> {
}