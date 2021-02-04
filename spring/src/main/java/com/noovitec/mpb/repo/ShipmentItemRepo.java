package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.ShipmentItem;

public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {
	
	@Query("select new com.noovitec.mpb.dto.KeyValueDto(shipItem.id, concat(s.number, ' (', i.number, ' - ', i.name, ')')) from ShipmentItem shipItem "
			+ "join shipItem.shipment ship "
			+ "join shipItem.saleItem si "
			+ "join si.sale s "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "where ship.id = :shipmentId")
	public List<KeyValueDto> findKvByShipment(Long shipmentId);

}