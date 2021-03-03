package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.custom.CustomScheduleEventRepo;

public interface ScheduleEventRepo extends JpaRepository<ScheduleEvent, Long>, CustomScheduleEventRepo {

	@Query(value = "select unitsScheduled from ScheduleEvent where id = :schedule_event_id")
	public Long getScheduledUnits(@Param("schedule_event_id") Long schedule_event_id);

	@Query(value = "select i.id from ScheduleEvent se "
			+ "join se.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "where se.id = :schedule_event_id")
	public Long getItemIdByScheduleEvent(@Param("schedule_event_id") Long schedule_event_id);

	@Query("select se from ScheduleEvent se "
			+ "join se.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "join i.itemComponents ic "
			+ "where se.date <= :date "
			+ "and ic.component.id = :component_id")
	public Long getByScheduleDateAndComponent(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("component_id") Long component_id);

	@Query("select sum(se.unitsScheduled) from ScheduleEvent se "
			+ "join se.itemPackaging ip "
			+ "join ip.item i "
			+ "where se.date >= :date "
			+ "and i.id = :item_id "
			+ "group by i.id")
	public Long getTotalItemScheduled(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id);

	@Query("select sum(se.unitsScheduled) from ScheduleEvent se "
			+ "join se.itemPackaging ip "
			+ "join ip.item i "
			+ "where se.date >= :date "
			+ "and i.id = :item_id "
			+ "and se.saleItem.sale.id = :sale_id "
			+ "group by i.id")
	public Long getTotalItemAndSaleScheduled(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @Param("item_id") Long item_id, @Param("sale_id") Long sale_id);

	@Query("select se from ScheduleEvent se "
			+ "join se.line line "
			+ "where se.date = :date "
			+ "and line.id = :line_id")
	public List<ScheduleEvent> findByDateAndLine(LocalDate date, Long line_id);

	@Query("select se from ScheduleEvent se "
			+ "join se.saleItem si "
			+ "join si.itemPackaging ip "
			+ "join ip.item i "
			+ "where i.id = :item_id")
	public List<ScheduleEvent> findByItem(@Param("item_id") Long item_id);
	
//	@Query("select se from ScheduleEvent se "
//			+ "where se.date = :date")
//	public List<ScheduleEvent> findByDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);
	
}