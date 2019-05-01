package com.noovitec.mpb.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.entity.ScheduleItem;

public interface ScheduleItemRepo extends JpaRepository<ScheduleItem, Long> {

	@Query(value = "select unitsScheduled from ScheduleItem where id = :schedule_item_id")
	public Long getScheduledUnits(@Param("schedule_item_id") Long schedule_item_id);

	/*
	 * select si.id, s.date, si.units_scheduled, si.item_id from Schedule_Item si
	 * join schedule s on s.id = si.schedule_id join item i on i.id = si.item_id
	 * join item_component ic on ic.item_id = i.id join component c on c.id =
	 * ic.component_id where s.date >= '2019-04-26' and c.id = '3'
	 */
	@Query("select si from ScheduleItem si "
			+ "join si.item i "
			+ "join i.itemComponents ic "
			+ "where si.schedule.date <= :date "
			+ "and ic.component.id = :component_id")
	public Long getByScheduleDateAndComponent(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("component_id") Long component_id);

	/*
	 *select sum(si.units_scheduled) from schedule_item si
		join schedule s on s.id = si.schedule_id
		where si.item_id = 11
		and s.date >= '2019-03-02'
		group by si.item_id; 
	 */
	@Query("select sum(si.unitsScheduled) from ScheduleItem si "
			+ "where si.schedule.date >= :date "
			+ "and si.item.id = :item_id "
			+ "group by si.item.id")
	public Long getUnitsScheduled(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id);

}