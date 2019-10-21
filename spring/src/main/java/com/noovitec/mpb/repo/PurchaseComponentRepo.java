package com.noovitec.mpb.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.PurchaseComponent;

public interface PurchaseComponentRepo extends JpaRepository<PurchaseComponent, Long> {

	@Query(value = "select pc from PurchaseComponent pc where pc.purchase.id = :purchase_id")
	List<PurchaseComponent> findByPurchase(@Param("purchase_id") Long purchase_id);

	@Query(value = "select pc from PurchaseComponent pc where pc.component.id = :component_id")
	List<PurchaseComponent> findByComponent(@Param("component_id") Long component_id);

	@Query(value = "select pc from PurchaseComponent pc where pc.purchase.id = :purchase_id and pc.component.id = :component_id")
	List<PurchaseComponent> findByPurchaseAndComponent(@Param("purchase_id") Long purchase_id, @Param("component_id") Long component_id);
}