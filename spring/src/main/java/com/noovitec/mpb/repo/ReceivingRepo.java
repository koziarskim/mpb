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

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(pc.component.id, sum(r.units)) "
			+ "from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "where r.etaDate <= :date and r.receivedDate is null "
			+ "group by pc.component.id")
	List<KeyValueDto> findComponentsInTransitToDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

	@Query("select new com.noovitec.mpb.dto.KeyValueDto(pc.component.id, sum(r.units)) "
			+ "from Receiving r "
			+ "join r.purchaseComponent pc "
			+ "where r.etaDate > :date and r.receivedDate is null "
			+ "group by pc.component.id")
	List<KeyValueDto> findReceivingFutureEta(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

	@Query("select r from Receiving r where r.receivedDate is null")
	List<Receiving> findReceivingInTransit();

}