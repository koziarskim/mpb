package com.noovitec.mpb.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.noovitec.mpb.dto.ItemAvailabilityDto;
import com.noovitec.mpb.dto.ItemDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.projection.ItemAvailabilityProjection;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ReceivingRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.repo.ScheduleRepo;

@RestController
@RequestMapping("/api")
class ScheduleRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleRest.class);
	private ScheduleRepo scheduleRepo;
	@Autowired
	private ItemRepo itemRepo;
	@Autowired
	private ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private ReceivingRepo receivingRepo;

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
			// Get all items that are available to schedule based on receiving.
			List<Long> items = new ArrayList<Long>();
			if(s.getId()!=null) {
				items.addAll(itemRepo.getItemsScheduled(s.getId()));
			}
			if(!items.isEmpty()) {
				for(ItemAvailabilityProjection iap : itemRepo.getItemsAvailabilityFiltered(s.getDate(), items)) {
					ItemAvailabilityDto dto = new ItemAvailabilityDto();
					dto.setId(iap.getId());
					dto.setUnitsToProduction(iap.getUnitsToProduction());
					dto.setUnitsToSchedule(iap.getUnitsToSchedule());
					Long itemUnits = itemRepo.getItemsScheduledToDate(s.getDate(), iap.getId());
					dto.setUnitsScheduled(itemUnits==null?0L:itemUnits);
					s.getItems().add(dto);
				}
			}
			result.add(s);
		}
		// Convert to List and sort.
		List<Schedule> list = new ArrayList<Schedule>(result);
		list.sort(Comparator.comparing(o -> o.getDate()));
		return list;
	}

//	private Collection<ItemDto> getByEta(Date date, Long item_id, boolean includeAll) {
//		Collection<ItemDto> dtos = new HashSet<ItemDto>();
//		Map<Long, Long> componentsInTransitToDate = new HashMap<Long, Long>();
//
//		List<KeyValueDto> componentsInTransitToDateList = receivingRepo.findComponentsInTransitToDate(date);
//
//		for (KeyValueDto keyValueDto : componentsInTransitToDateList) {
//			componentsInTransitToDate.put(keyValueDto.getKey(), (Long) keyValueDto.getValue());
//		}
//
//		Collection<Item> items = new HashSet<Item>();
//		if (item_id == null) {
//			items.addAll(itemRepo.findAll());
//		} else {
//			items.add(itemRepo.getOne(item_id));
//		}
//
//		for (Item item : items) {
//			ItemDto dto = new ItemDto();
//			dto.setId(item.getId());
//			dto.setNumber(item.getNumber());
//			int itemsReadySchedule = 0;
//			int itemsReadyProduction = 0;
//			for (ItemComponent ic : item.getItemComponents()) {
//				Long componentUnitsInTransit = componentsInTransitToDate.get(ic.getComponent().getId());
//
//				float icReadyProductionFloat = ic.getComponent().getUnitsOnStack() / ic.getUnits();
//				float icReadyScheduleFloat = (ic.getComponent().getUnitsOnStack() + (componentUnitsInTransit == null ? 0 : componentUnitsInTransit))
//						/ ic.getUnits();
//				// Production
//				int icReadyProduction = this.roundToInt(icReadyProductionFloat);
//				if (itemsReadyProduction == 0 || icReadyProduction < itemsReadyProduction) {
//					itemsReadyProduction = icReadyProduction;
//				}
//				// Schedule
//				int icReadySchedule = this.roundToInt(icReadyScheduleFloat);
//				if (itemsReadySchedule == 0 || icReadySchedule < itemsReadySchedule) {
//					itemsReadySchedule = icReadySchedule;
//				}
//			}
//			Long totalItemSchedule = scheduleEventRepo.getTotalItemScheduled(LocalDate.now(), item.getId());
//			dto.setTotalItemScheduled(totalItemSchedule == null ? 0 : totalItemSchedule.intValue());
//			dto.setUnitsReadySchedule(itemsReadySchedule);
//			dto.setUnitsReadyProduction(itemsReadyProduction);
//			if (!includeAll) {
//				if (dto.getUnitsReadySchedule() > 0) {
//					dtos.add(dto);
//				}
//			} else {
//				dtos.add(dto);
//			}
//		}
//		return dtos;
//	}
//
//	private int roundToInt(float unitsFloat) {
//		int units = 0;
//		if (unitsFloat > 0) {
//			units = new BigDecimal(unitsFloat).setScale(0, RoundingMode.DOWN).intValue();
//		} else {
//			units = new BigDecimal(unitsFloat).setScale(0, RoundingMode.UP).intValue();
//		}
//		return units;
//	}

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