package com.noovitec.mpb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.PurchaseComponent;

public interface PurchaseComponentRepo extends JpaRepository<PurchaseComponent, Long> {

	@Query(value = "select pc from PurchaseComponent pc where pc.purchase.id = :purchase_id")
	Optional<PurchaseComponent> findByPurchase(@Param("purchase_id") Long purchase_id);
}