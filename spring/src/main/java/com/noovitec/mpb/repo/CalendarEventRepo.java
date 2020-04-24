package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.CalendarEvent;

public interface CalendarEventRepo extends JpaRepository<CalendarEvent, Long> {
}