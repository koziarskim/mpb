package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.custom.CustomShipmentRepo;

public interface ShipmentRepo extends PagingAndSortingRepository<Shipment, Long>, CustomShipmentRepo {

	@Query("select ship from Shipment ship where ship.id in (:ids)")
	Page<Shipment> findPage(Pageable pageable, List<Long> ids);

	@Query("select count(*) from Shipment where date = :date")
	Long getLastNumber(LocalDate date);

}