package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itextpdf.text.DocumentException;
import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.repo.ScheduleRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ScheduleRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleRest.class);
	private ScheduleRepo scheduleRepo;

	public ScheduleRest(ScheduleRepo scheduleRepo) {
		this.scheduleRepo = scheduleRepo;
	}

	@GetMapping("/schedule")
	Collection<Schedule> getAll() {
		return scheduleRepo.findAll();
	}

	@GetMapping("/schedule/date/{date}")
	Collection<Schedule> findByDate(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
		localDateTime.plusDays(7);
		Date dateTo = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Collection<Schedule> result = scheduleRepo.findByRange(date, dateTo);
		return result;
	}

	@GetMapping("/schedule/{id}")
	ResponseEntity<Schedule> get(@PathVariable Long id) {
		Optional<Schedule> result = scheduleRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/schedule")
	ResponseEntity<Schedule> post(@RequestBody(required = false) Schedule schedule)
			throws URISyntaxException, JsonParseException, JsonMappingException, IOException, DocumentException {
		if (schedule == null) {
			schedule = new Schedule();
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