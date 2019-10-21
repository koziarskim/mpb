package com.noovitec.mpb.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.Receiving;

public interface ReceivingRepo extends JpaRepository<Receiving, Long> {

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(pc.component.id, sum(r.units)) "
			+ "from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "where r.etaDate <= :date and r.receivingDate is null "
			+ "group by pc.component.id")
	List<KeyValueDto> findComponentsInTransitToDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(pc.component.id, sum(r.units)) "
			+ "from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "where r.etaDate > :date and r.receivingDate is null "
			+ "group by pc.component.id")
	List<KeyValueDto> findReceivingFutureEta(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

	@Query("select r from Receiving r")
	Page<Receiving> findPage(Pageable pageable);

	@Query("select r from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "join pc.purchase p "
			+ "where p.id = :purchase_id")
	Page<Receiving> findByPurchase(Pageable pageable, Long purchase_id);

	@Query("select r from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "join pc.component c "
			+ "where c.id = :component_id")
	Page<Receiving> findByComponent(Pageable pageable, Long component_id);

	@Query("select r from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "join pc.purchase p "
			+ "join pc.component c "
			+ "where p.id = :purchase_id "
			+ "and c.id = :component_id")
	Page<Receiving> findByPurchaseAndComponent(Pageable pageable, Long purchase_id, Long component_id);
}