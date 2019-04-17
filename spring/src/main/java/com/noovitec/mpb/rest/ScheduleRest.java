package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itextpdf.text.DocumentException;
import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.entity.ScheduleItem;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ScheduleRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ScheduleRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleRest.class);
	private ScheduleRepo scheduleRepo;
	@Autowired
	private ItemRepo itemRepo;

	public ScheduleRest(ScheduleRepo scheduleRepo) {
		this.scheduleRepo = scheduleRepo;
	}

	@GetMapping("/schedule")
	Collection<Schedule> getAll() {
		return scheduleRepo.findAll();
	}

	@GetMapping("/schedule/date/{date}")
	Collection<Schedule> findByDate(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		int days = 6;
		LocalDate dateTo = date.plusDays(days);
		Collection<Schedule> schedules = scheduleRepo.findByRange(date, dateTo);
		Map<LocalDate, Schedule> scheduleMap = new HashMap<LocalDate, Schedule>();
		Collection<Schedule> result = new HashSet<Schedule>();
		for (Schedule schedule : schedules) {
			scheduleMap.put(schedule.getDate(), schedule);
		}
		for (int day = 0; day <= days; day++) {
			LocalDate d = date.plusDays(day);
			Schedule s = scheduleMap.get(d);
			if (s == null) {
				s = new Schedule();
				s.setDate(d);
			}
			result.add(s);
		}
		return result;
	}

	@GetMapping("/schedule/{id}")
	ResponseEntity<Schedule> get(@PathVariable Long id) {
		Optional<Schedule> result = scheduleRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/schedule")
	ResponseEntity<Schedule> post(@RequestBody Schedule schedule) {
		if (schedule == null) {
			schedule = new Schedule();
		}
		for(ScheduleItem si : schedule.getScheduleItems()) {
			si.setSchedule(schedule);
		}
		Schedule result = scheduleRepo.save(schedule);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/schedule/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		scheduleRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}