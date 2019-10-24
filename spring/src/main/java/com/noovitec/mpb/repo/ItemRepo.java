package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Item;

public interface ItemRepo extends PagingAndSortingRepository<Item, Long> {
	Item findByName(String name);

	@Query(value = "select i.* from Item i order by i.id desc limit 1", nativeQuery = true)
	Item getLast();
	
	@Query(value = "select new com.noovitec.mpb.dto.KeyValueDto(i.id, i.name) from Item i")
	public List<KeyValueDto> getAllKeyValueDtos();

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStock, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "left join SaleItem si on si.item.id = i.id "
			+ "left join ScheduleEvent se on se.saleItem.id = si.id "
			+ "left join Production p on p.scheduleEvent.id = se.id "
			+ "group by i.id, c.id, b.id")
	List<ItemListDto> getItemListDto();

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStock, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
			+ "left join Category c on c.id = i.category.id "
			+ "left join Brand b on b.id = i.brand.id "
			+ "left join SaleItem si on si.item.id = i.id "
			+ "left join ScheduleEvent se on se.saleItem.id = si.id "
			+ "left join Production p on p.scheduleEvent.id = se.id "
			+ "group by i.id, c.id, b.id")
	Page<ItemListDto> getItemListDtoPageable(Pageable pageable);

	@Query("select new com.noovitec.mpb.dto.ItemListDto(i.id, i.number, i.name, b.name, c.name, i.unitsOnStock, sum(si.units), sum(se.unitsScheduled), sum(p.unitsProduced)) from Item i "
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
	Page<Item> findPage(Pageable pageable);

	@Query("select i from Item i "
			+ "join Category c on c.id = i.category.id "
			+ "join Brand b on b.id = i.brand.id "
			+ "where upper(i.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(i.number) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(b.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Item> findPageByItem(Pageable pageable, String searchKey);

	@Query("select i from Item i "
			+ "join i.itemComponents ic "
			+ "join ic.component c "
			+ "where upper(c.name) LIKE CONCAT('%',UPPER(:searchKey),'%') "
			+ "or upper(c.number) LIKE CONCAT('%',UPPER(:searchKey),'%')")
	Page<Item> findPageByComponent(Pageable pageable, String searchKey);

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
	@Query("update Item i set i.unitsOnStock = :units where i.id = :item_id")
	void updateUnitsOnStock(@Param("units") Long units, @Param("item_id") Long item_id);
}