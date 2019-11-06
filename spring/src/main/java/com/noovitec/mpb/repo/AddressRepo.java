package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Address;

public interface AddressRepo extends JpaRepository<Address, Long> {
	
	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(a.id, concat(a.street, ', ', a.city)) from Customer cu "
			+ "join cu.addresses a "
			+ "where cu.id = :customer_id")
	List<KeyValueDto> findKvByCustomer(Long customer_id);

	@Query("select distinct new com.noovitec.mpb.dto.KeyValueDto(a.id, concat(a.street, ', ', a.city)) from Address a "
			+ "where upper(a.type) = upper(:type)")
	List<KeyValueDto> findKvByType(String type);

}