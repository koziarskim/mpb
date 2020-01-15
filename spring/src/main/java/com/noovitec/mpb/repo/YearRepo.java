package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Year;

public interface YearRepo extends JpaRepository<Year, Long> {
}