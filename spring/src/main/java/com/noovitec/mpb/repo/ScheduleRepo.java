package com.noovitec.mpb.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.noovitec.mpb.entity.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

	@Query("select s from Schedule s where s.date >= :dateFrom and s.date <= :dateTo")
	List<Schedule> findByRange(@Param("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
			@Param("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo);

	@Query("select s from Schedule s where s.date = :date")
	List<Schedule> findByDate(@Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

}