package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Brand;
import com.noovitec.mpb.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}