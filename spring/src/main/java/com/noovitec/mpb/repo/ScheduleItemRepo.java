package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.ScheduleItem;

public interface ScheduleItemRepo extends JpaRepository<ScheduleItem, Long> {

	@Query(value = "select unitsScheduled from ScheduleItem where id = :schedule_item_id")
	public Long getScheduledUnits(@Param("schedule_item_id") Long schedule_item_id);
}