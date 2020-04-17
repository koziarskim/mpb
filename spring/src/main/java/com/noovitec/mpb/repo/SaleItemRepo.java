package com.noovitec.mpb.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.custom.CustomSaleItemRepo;

public interface SaleItemRepo extends PagingAndSortingRepository<SaleItem, Long>, CustomSaleItemRepo {

	
	@Query("select si from SaleItem si "
			+ "join si.sale s "
			+ "where s.number like '%---%'")
	List<SaleItem> findByHyphen();

	@Query("select s from Sale s "
			+ "where s.number = :saleNumber")
	Sale getByNumber(String saleNumber);

	@Query("select si from SaleItem si where si.id in (:ids)")
	Page<SaleItem> findPage(Pageable pageable, List<Long> ids);

	@Query(value = "select si " + "from SaleItem si " + "where si.id = :sale_item_id ")
	public Optional<SaleItem> getSaleItemById(@Param("sale_item_id") Long sale_item_id);

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(si.id, si.item.number, si.item.name) " + "from SaleItem si " + "where si.sale.id = :sale_id ")
	public List<KeyValueDto> findSaleItemsBySale(@Param("sale_id") Long sale_id);

	@Query(value = "select si from SaleItem si join si.sale s join s.customer cu where cu.id = :customerId")
	public List<SaleItem> findByCustomer(Long customerId);

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(s.number, ' (', c.name, ')' )) "
			+ "from SaleItem si "
			+ "join si.sale s "
			+ "join s.customer c "
			+ "where si.item.id = :item_id ")
	public List<KeyValueDto> findKvByItem(@Param("item_id") Long item_id);

	@Query(value = "select si from SaleItem si "
			+ "join si.sale s "
			+ "join s.customer c "
			+ "where si.item.id = :item_id "
			+ "and (si.unitsReturned > 0 or si.unitsOnStock > 0) "
			+ "order by si.unitsReturned desc, si.unitsOnStock desc")
	public List<SaleItem> findKvTrasferByItem(@Param("item_id") Long item_id);

	@Query(value = "select distinct new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(s.number, ' (', c.name, ')' )) "
			+ "from SaleItem si "
			+ "join si.sale s "
			+ "join s.customer c "
			+ "where si.item.id = :itemId "
			+ "and c.id = :customerId")
	public List<KeyValueDto> findKvByItemAndCustomer(Long itemId, Long customerId);

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(s.number, ' (', i.number, ' - ', i.name, ')')) " + "from SaleItem si "
			+ "join si.sale s " + "join s.customer cu " + "join si.item i " + "where cu.id = :customerId " + "and si.units > si.unitsShipped")
	public List<KeyValueDto> findKvByCustomer(@Param("customerId") Long customerId);

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(si.sale.number, ' (', si.item.number, ')')) from SaleItem si "
			+ "join si.sale s "
			+ "order by s.number asc")
	public List<KeyValueDto> findAllKvs();

	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(si.id, concat(si.sale.number, ' (', si.item.number, ')')) from SaleItem si "
			+ "join si.sale s "
			+ "join si.shipmentItems shipItem "
			+ "join shipItem.shipment ship "
			+ "where ship.id = :shipmentId "
			+ "order by s.number asc")
	public List<KeyValueDto> findAllKvsByShipment(Long shipmentId);

	@Query("select si from SaleItem si where si.id in (:ids)")
	public List<SaleItem> findAllByIds(Long[] ids);
	
	@Transactional
	@Modifying
	@Query("update SaleItem si set si.sale.id = :saleId where si.id = :saleItemId")
	public void moveSaleItem(Long saleItemId, Long saleId);

}