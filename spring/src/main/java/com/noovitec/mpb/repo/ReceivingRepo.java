package com.noovitec.mpb.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Receiving;

public interface ReceivingRepo extends JpaRepository<Receiving, Long> {

	/*
		select pc.component_id, sum(r.units) from receiving r
		join purchase_component pc on pc.id = r.purchase_component_id
		join item_component ic on ic.component_id = pc.component_id
		where r.eta_date <= '2019-05-07 19:00:00'
		and r.received_date is null
		group by pc.component_id
	 */
	@Query("select new com.noovitec.mpb.dto.KeyValueDto(ic.component.id, sum(r.units)) "
			+ "from Receiving r "
			+ "join r.purchaseComponent.component.itemComponents ic "
			+ "where r.etaDate <= :date and r.receivedDate is null "
			+ "group by ic.component.id")
	List<KeyValueDto> findReceivingByEta(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

}