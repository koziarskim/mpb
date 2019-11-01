package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.entity.ScheduleEvent;

public interface ScheduleEventRepo extends JpaRepository<ScheduleEvent, Long> {

	@Query(value = "select unitsScheduled from ScheduleEvent where id = :schedule_event_id")
	public Long getScheduledUnits(@Param("schedule_event_id") Long schedule_event_id);

	@Query(value = "select se.saleItem.item.id from ScheduleEvent se where se.id = :schedule_event_id")
	public Long getItemIdByScheduleEvent(@Param("schedule_event_id") Long schedule_event_id);

	@Query("select se from ScheduleEvent se "
			+ "join se.saleItem.item i "
			+ "join i.itemComponents ic "
			+ "where se.schedule.date <= :date "
			+ "and ic.component.id = :component_id")
	public Long getByScheduleDateAndComponent(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("component_id") Long component_id);

	@Query("select sum(se.unitsScheduled) from ScheduleEvent se "
			+ "where se.schedule.date >= :date "
			+ "and se.saleItem.item.id = :item_id "
			+ "group by se.saleItem.item.id")
	public Long getTotalItemScheduled(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id);

	@Query("select sum(se.unitsScheduled) from ScheduleEvent se "
			+ "where se.schedule.date >= :date "
			+ "and se.saleItem.item.id = :item_id "
			+ "and se.saleItem.sale.id = :sale_id "
			+ "group by se.saleItem.item.id")
	public Long getTotalItemAndSaleScheduled(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id, @Param("sale_id") Long sale_id);

	@Query("select se from ScheduleEvent se "
			+ "join se.schedule sch "
			+ "join se.line line "
			+ "where sch.date = :date "
			+ "and line.id = :line_id")
	public List<ScheduleEvent> findByDateAndLine(LocalDate date, Long line_id);

	@Query("select se from ScheduleEvent se "
			+ "where se.saleItem.item.id = :item_id")
	public List<ScheduleEvent> findByItem(@Param("item_id") Long item_id);
	
	@Query("select se from ScheduleEvent se "
			+ "join Schedule s on s.id = se.schedule.id "
			+ "where s.date = :date")
	public List<ScheduleEvent> findByDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

}