package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.dto.ItemAvailabilityDto;
import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.projection.ItemAvailabilityProjection;
import com.noovitec.mpb.entity.Item;

public interface ItemRepo extends PagingAndSortingRepository<Item, Long> {
	Item findByName(String name);

	@Query(value = "select i.* from Item i order by i.id desc limit 1", nativeQuery = true)
	Item getLast();
	
	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) " + "from Item i")
	public List<KeyValueDto> getAllKeyValueDtos();

	@Query("select i " + "from Item i " + "join i.saleItems si " + "join si.sale s " + "join s.purchaseSales ps " + "left join i.brand b "
			+ "left join i.category c " + "join ps.purchase.purchaseComponents pc " + "where ps.purchase.id = :purchase_id")
	List<Item> getPurchaseItems(@Param("purchase_id") Long purchase_id);

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStack, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "left join SaleItem si on si.item.id = i.id "
			+ "left join ScheduleEvent se on se.saleItem.id = si.id "
			+ "left join Production p on p.scheduleEvent.id = se.id "
			+ "group by i.id, c.id, b.id")
	List<ItemListDto> getItemListDto();

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStack, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "left join SaleItem si on si.item.id = i.id "
			+ "left join ScheduleEvent se on se.saleItem.id = si.id "
			+ "left join Production p on p.scheduleEvent.id = se.id "
			+ "group by i.id, c.id, b.id")
	Page<ItemListDto> getItemListDtoPageable(Pageable pageable);

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStack, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "left join SaleItem si on si.item.id = i.id "
			+ "left join ScheduleEvent se on se.saleItem.id = si.id "
			+ "left join Production p on p.scheduleEvent.id = se.id "
			+ "where upper(i.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(b.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "group by i.id, c.id, b.id")
	Page<ItemListDto> getItemListDtoPageable(Pageable pageable, String searchKey);

	@Query("select i from Item i")
	Page<Item> getItemsPageable(Pageable pageable);

	@Query("select i from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "where upper(i.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(b.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Item> getItemsPageable(Pageable pageable, String searchKey);
	
	/*
	select tmp.i_id, min(tmp.units) from (
		select i.id as i_id,
		((c.units_on_stack + sum(case when r.units is null then 0 else r.units end))/max(ic.units)) as units from item i
		join item_component ic on ic.item_id = i.id
		join component c on c.id = ic.component_id
		left join purchase_component pc on pc.component_id = c.id
		left join receiving r on r.purchase_component_id = pc.id and r.received_date is null and r.eta_date < '2019-06-1'																			 
		group by c.id, i.id
		order by i.id asc) as tmp
	group by tmp.i_id
	 */
	@Query(value = ""
			+ "select tmp.i_id as id, tmp.us as unitsScheduled, min(tmp.unitsToSchedule) as unitsToSchedule, min(tmp.unitsToProduction) as unitsToProduction "
				+ "from (select i.id as i_id, i.units_scheduled as us, "
				+ "((c.units_on_stack + sum(case when r.units is null then 0 else r.units end))/max(ic.units)) as unitsToSchedule, "
				+ "((c.units_on_stack)/max(ic.units)) as unitsToProduction "
				+ "from item i "
				+ "join item_component ic on ic.item_id = i.id "
				+ "join component c on c.id = ic.component_id "
				+ "left join purchase_component pc on pc.component_id = c.id "
				+ "left join receiving r on r.purchase_component_id = pc.id and r.received_date is null and r.eta_date <= :date "
				+ "where "
				+ "(case when 0 in :itemIds then true else i.id in :itemIds end) "
				+ "group by c.id, i.id "
				+ "order by i.id asc) as tmp "
			+ "group by tmp.i_id, tmp.us", nativeQuery = true)
	List<ItemAvailabilityProjection> getItemsAvailabilityFiltered(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("itemIds") List<Long> itemIds);
	
	@Query("select distinct i.id from Item i "
			+ "join i.saleItems si "
			+ "join si.scheduleEvents se "
			+ "where se.schedule.id = :schedule_id")
	List<Long> getItemsScheduled(@Param("schedule_id") Long schedule_id);
	
	@Query(value = "select sum(se.unitsScheduled) from ScheduleEvent se "
			+ "where se.schedule.date <= :date "
			+ "and se.saleItem.item.id = :item_id "
//			+ "and se.eventCompleted = false "
			+ "group by se.saleItem.item.id")
	Long getItemsScheduledToDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id);
	
	@Modifying
	@Transactional
	@Query("update Item i set i.unitsOnStack = :units where i.id = :item_id")
	void updateUnitsOnStock(@Param("units") Long units, @Param("item_id") Long item_id);
}