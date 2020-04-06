package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.Year;

public interface YearRepo extends JpaRepository<Year, Long> {
	
	@Query("select y from Year y where y.name = :yearName")
	public List<Year> findByName(String yearName);
}