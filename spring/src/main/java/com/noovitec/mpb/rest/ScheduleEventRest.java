package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;

@RestController
@RequestMapping("/api")
class ScheduleEventRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleEventRest.class);
	private ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private ComponentRepo componentRepo;
	@Autowired
	private ItemRepo itemRepo;

	public ScheduleEventRest(ScheduleEventRepo scheduleEventRepo) {
		this.scheduleEventRepo = scheduleEventRepo;
	}

	@GetMapping("/scheduleEvent")
	Collection<ScheduleEvent> getAll() {
		return scheduleEventRepo.findAll();
	}

	@GetMapping("/scheduleEvent/{id}")
	ResponseEntity<ScheduleEvent> get(@PathVariable Long id) {
		Optional<ScheduleEvent> result = scheduleEventRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/scheduleEvent")
	ResponseEntity<ScheduleEvent> post(@RequestBody ScheduleEvent scheduleEvent) {
		if (scheduleEvent == null) {
			scheduleEvent = new ScheduleEvent();
		}
		for (Production production : scheduleEvent.getProductions()) {
			production.setScheduleEvent(scheduleEvent);
		}
		// Get this before calling save.
//		Long existingUnitsScheduled = scheduleEventRepo.getScheduledUnits(scheduleEvent.getId());
		ScheduleEvent result = scheduleEventRepo.save(scheduleEvent);
//		if (result.getUnitsScheduled() != null) {
//			Item item = itemRepo.getOne(result.getItem().getId());
//			// Positive or negative
//			Long itemUnits = result.getUnitsScheduled() - (existingUnitsScheduled == null ? 0L : existingUnitsScheduled);
//			item.addUnitsScheduled(itemUnits);
//			itemRepo.save(item);
//			for (ItemComponent ic : item.getItemComponents()) {
//				// Positive or negative
//				Long componentUnits = ic.getUnits() * itemUnits;
//				Component component = ic.getComponent();
//				// Add/Substract based on positive or negative value.
//				component.addUnitsReserved(componentUnits);
//				componentRepo.save(component);
//			}
//		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/scheduleEvent/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(id);
		if (scheduleEvent.getUnitsScheduled() != null) {
			Item item = itemRepo.getOne(scheduleEvent.getSaleItem().getItem().getId());
			for (ItemComponent ic : item.getItemComponents()) {
				Long componentUnits = ic.getUnits() * scheduleEvent.getUnitsScheduled();
				Component component = ic.getComponent();
				// Subtract
				component.addUnitsReserved(componentUnits * (-1));
				componentRepo.save(component);
			}
			item.addUnitsScheduled(scheduleEvent.getUnitsScheduled() * (-1));
			itemRepo.save(item);

		}
		scheduleEventRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}