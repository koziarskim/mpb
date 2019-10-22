package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Purchase;

public interface PurchaseRepo extends JpaRepository<Purchase, Long> {

	@Query("select p from Purchase p join p.purchaseComponents pc where pc.component.id = :component_id")
	List<Purchase> findByComponent(@Param("component_id") Long component_id);

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(p.id, p.number) from Purchase p")
	public List<KeyValueDto> findAllKv();

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(p.id, p.number) from Purchase p "
			+ "join p.purchaseComponents pc "
			+ "where pc.component.id = (:component_id)")
	public List<KeyValueDto> findKvByComponnet(@Param("component_id") Long component_id);

	
	@Query("select p from Purchase p")
	Page<Purchase> findPage(Pageable pageable);

	@Query("select distinct p from Purchase p "
			+ "where upper(p.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(p.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Purchase> findPageByPurchase(Pageable pageable, String searchKey);

	@Query("select distinct p from Purchase p "
			+ "join p.purchaseComponents pc "
			+ "join pc.component c "
			+ "where upper(c.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Purchase> findPageByComponent(Pageable pageable, String searchKey);
	
	@Modifying
	@Transactional
	@Query("update Purchase p set p.receivingDate = :receivingDate where p.id = :purchase_id")
	void updateReceivingDate(@Param("receivingDate") LocalDate receivingDate, @Param("purchase_id") Long purchase_id);

}