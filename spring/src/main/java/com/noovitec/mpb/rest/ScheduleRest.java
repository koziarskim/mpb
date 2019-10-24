package com.noovitec.mpb.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.EventDto;
import com.noovitec.mpb.dto.LineDto;
import com.noovitec.mpb.dto.ScheduleDto;
import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ScheduleRepo;

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

	@GetMapping("/schedule/single/date/{date}")
	Schedule getByDate(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		List<Schedule> result = scheduleRepo.findByDate(date);
		if(result.size()>0) {
			return result.get(0);
		}
		return null;
	}
	
	@GetMapping("/schedule/date/{date}")
	Collection<ScheduleDto> findByDate(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		LocalDate dateTo = date.plusDays(6);
		Collection<Schedule> schedules = scheduleRepo.findByRange(date, dateTo);
		List<ScheduleDto> scheduleDtos = new ArrayList<ScheduleDto>();
		for (Schedule schedule : schedules) {
			ScheduleDto scheduleDto = new ScheduleDto();
			scheduleDto.setDate(schedule.getDate());
			scheduleDto.setId(schedule.getId());
			for(ScheduleEvent se: schedule.getScheduleEvents()) {
				LineDto lineDto = scheduleDto.getLines().stream().filter(existingDto -> existingDto.getNumber() == se.getLine().getNumber()).findAny().orElse(null);
				if(lineDto == null) {
					lineDto = new LineDto();
					lineDto.setNumber(se.getLine().getNumber());
					scheduleDto.getLines().add(lineDto);
				}
				EventDto eventDto = new EventDto();
				eventDto.setId(se.getId());
				eventDto.setItemName(se.getSaleItem().getItem().getName()+" - "+se.getSaleItem().getSale().getCustomer().getName());
				lineDto.getEvents().add(eventDto);
			}
			scheduleDtos.add(scheduleDto);
		}
		// Convert to List and sort.
		List<ScheduleDto> list = new ArrayList<ScheduleDto>(scheduleDtos);
		list.sort(Comparator.comparing(o -> o.getDate()));
		return list;
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
		if(schedule.getId() == null && schedule.getDate()!=null) {
			List<Schedule> existingSchedules = scheduleRepo.findByDate(schedule.getDate());
			if(existingSchedules !=null && existingSchedules.size()>0) {
				schedule = existingSchedules.get(0);
			}
		}
		for (ScheduleEvent se : schedule.getScheduleEvents()) {
			se.setSchedule(schedule);
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