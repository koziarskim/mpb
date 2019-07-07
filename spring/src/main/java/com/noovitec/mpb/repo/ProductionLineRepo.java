package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.entity.ProductionLine;

public interface ProductionLineRepo extends JpaRepository<ProductionLine, Long> {

	@Query("select pl from ProductionLine pl where pl.dateStarted = :dateStarted")
	List<ProductionLine> findByDateStarted(@Param("dateStarted") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStarted);

}