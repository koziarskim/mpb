package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.Purchase;

public interface PurchaseRepo extends JpaRepository<Purchase, Long> {

	@Query("select p from Purchase p join p.purchaseComponents pc where pc.component.id = :component_id")
	List<Purchase> findByComponent(@Param("component_id") Long component_id);

}