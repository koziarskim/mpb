package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.ShipmentItem;

public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {

}