package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.ScheduleItem;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ScheduleItemRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ScheduleItemRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleItemRest.class);
	private ScheduleItemRepo scheduleItemRepo;
	@Autowired
	private ComponentRepo componentRepo;
	@Autowired
	private ItemRepo itemRepo;

	public ScheduleItemRest(ScheduleItemRepo scheduleItemRepo) {
		this.scheduleItemRepo = scheduleItemRepo;
	}

	@GetMapping("/scheduleItem")
	Collection<ScheduleItem> getAll() {
		return scheduleItemRepo.findAll();
	}

	@GetMapping("/scheduleItem/{id}")
	ResponseEntity<ScheduleItem> get(@PathVariable Long id) {
		Optional<ScheduleItem> result = scheduleItemRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/scheduleItem")
	ResponseEntity<ScheduleItem> post(@RequestBody ScheduleItem scheduleItem) {
		if (scheduleItem == null) {
			scheduleItem = new ScheduleItem();
		}
		boolean newEntity = scheduleItem.getId()==null;
		ScheduleItem result = scheduleItemRepo.save(scheduleItem);
		//Update components on stack only on creating new scheduleItem (not when updating). To update, it has to deleted first.
		if (newEntity && result.getUnitsScheduled() != null) {
			Item item = itemRepo.getOne(result.getItem().getId());
			Long itemUnits = result.getUnitsScheduled() - (result.getUnitsProduced() == null ? 0L : result.getUnitsProduced());
			for (ItemComponent ic : item.getItemComponents()) {
				Long componentUnits = ic.getUnits() * itemUnits;
				Component component = ic.getComponent();
				// Add already subtracted items if produced more that scheduled.
				if (componentUnits < 0) {
					component.addUnitsOnStack(componentUnits.intValue() * (-1));
				}
				component.subtractUnitsOnStack(componentUnits.intValue());
				componentRepo.save(component);
			}
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/scheduleItem/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ScheduleItem scheduleItem = scheduleItemRepo.getOne(id);
		if (scheduleItem.getUnitsScheduled() != null) {
			Item item = itemRepo.getOne(scheduleItem.getItem().getId());
			Long itemUnits = scheduleItem.getUnitsScheduled() - (scheduleItem.getUnitsProduced() == null ? 0L : scheduleItem.getUnitsProduced());
			for (ItemComponent ic : item.getItemComponents()) {
				Long componentUnits = ic.getUnits() * itemUnits;
				Component component = ic.getComponent();
				// Add already subtracted items if produced more that scheduled.
				if (componentUnits < 0) {
					component.addUnitsOnStack(componentUnits.intValue() * (-1));
				}
				component.subtractUnitsOnStack(componentUnits.intValue());
				componentRepo.save(component);
			}
		}
		scheduleItemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}