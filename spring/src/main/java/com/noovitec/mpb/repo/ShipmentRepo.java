package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.custom.CustomShipmentRepo;

public interface ShipmentRepo extends JpaRepository<Shipment, Long>, CustomShipmentRepo {

	@Query("select ship from Shipment ship where ship.id in :shipmentIds")
	List<Shipment> findByIds(List<Long> shipmentIds);
	
	@Query("select count(*) from Shipment where date(created) = :date")
	Long getLastNumber(LocalDate date);
	
	@Query("select ship from Shipment ship where ship.ready = true "
			+ "and ship.shippingDate is not null "
			+ "and ship.shippingTime is not null "
			+ "and ship.shippingDate >= :startDate "
			+ "and ship.shippingDate <= :endDate ")
	List<Shipment> getReadyToShip(LocalDate startDate, LocalDate endDate);

	@Query(value="select ship.id from Shipment ship where ship.number = :number")
	public Long getIdByNumber(String number);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(ship.id, ship.number) from Shipment ship "
			+ "join ship.customer cu "
			+ "where cu.id = :customerId")
	public List<KeyValueDto> findKvByCustomer(Long customerId);

	@Query(value="select new com.noovitec.mpb.dto.KeyValueDto(ship.id, ship.number) from Shipment ship")
	public List<KeyValueDto> findKvs();

}