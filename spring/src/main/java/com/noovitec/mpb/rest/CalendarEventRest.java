package com.noovitec.mpb.rest;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.CalendarEventDto;
import com.noovitec.mpb.entity.CalendarEvent;
import com.noovitec.mpb.repo.CalendarEventRepo;

@RestController
@RequestMapping("/api")
class CalendarEventRest {

	private final Logger log = LoggerFactory.getLogger(CalendarEventRest.class);
	private CalendarEventRepo calendarEventRepo;

	CalendarEventRest(CalendarEventRepo calendarEventRepo) {
		this.calendarEventRepo = calendarEventRepo;
	}

	@GetMapping("/calendarEvent")
	Collection<CalendarEventDto> getAll() {
		List<CalendarEvent> events = calendarEventRepo.findAll();
		List<CalendarEventDto> dtos = new ArrayList<CalendarEventDto>();
		for(CalendarEvent event : events) {
			CalendarEventDto dto = new CalendarEventDto();
			DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd");
			DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
			dto.setId(event.getId());
			dto.setStart(event.getStartDate().format(df)+" "+event.getStartTime().format(tf));
			dto.setEnd(event.getEndDate().format(df)+" "+event.getEndTime().format(tf));
			dto.setHeading1(event.getHeading1());
			dto.setHeading2(event.getHeading2());
			dto.setLine1(event.getLine1());
			dto.setLine2(event.getLine2());
			dto.setLine3(event.getLine3());
			dto.setType(event.getType());
			dtos.add(dto);
		}
		return dtos;
	}

	@PostMapping("/calendarEvent")
	ResponseEntity<CalendarEvent> post(@RequestBody CalendarEvent calendarEvent) {
		calendarEvent = calendarEventRepo.save(calendarEvent);
		return ResponseEntity.ok().body(calendarEvent);
	}

	@DeleteMapping("/calendarEvent/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		calendarEventRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
}