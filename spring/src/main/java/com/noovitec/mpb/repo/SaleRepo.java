package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.SaleDto;
import com.noovitec.mpb.entity.Sale;

public interface SaleRepo extends JpaRepository<Sale, Long> {

	@Query("select new com.noovitec.mpb.dto.SaleDto(s.id, s.number, s.date, s.customer.name, s.shippingAddress.dc) from Sale s")
	public List<SaleDto> findAllByPurchase(Long purchase_id);
}