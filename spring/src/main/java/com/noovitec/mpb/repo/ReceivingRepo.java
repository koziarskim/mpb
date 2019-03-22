package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Receiving;

public interface ReceivingRepo extends JpaRepository<Receiving, Long> {
}