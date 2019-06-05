package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Shipment;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {

}