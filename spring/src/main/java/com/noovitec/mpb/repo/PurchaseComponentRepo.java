package com.noovitec.mpb.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.PurchaseComponent;

public interface PurchaseComponentRepo extends JpaRepository<PurchaseComponent, Long> {

	@Query(value = "select pc from PurchaseComponent pc where pc.purchase.id = :purchase_id")
	Collection<PurchaseComponent> findByPurchase(@Param("purchase_id") Long purchase_id);

	@Query(value = "select pc from PurchaseComponent pc where pc.component.id = :component_id")
	Collection<PurchaseComponent> findByComponent(@Param("component_id") Long component_id);
}