package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Vendor;

public interface VendorRepo extends JpaRepository<Vendor, Long> {
}