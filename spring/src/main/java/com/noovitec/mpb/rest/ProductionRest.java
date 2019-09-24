package com.noovitec.mpb.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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

import com.noovitec.mpb.dto.ItemTreeDto;
import com.noovitec.mpb.dto.LineTreeDto;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ProductionRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;

@RestController
@RequestMapping("/api")
class ProductionRest {

	private final Logger log = LoggerFactory.getLogger(ProductionRest.class);
	private ProductionRepo productionRepo;
	@Autowired
	private ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private ComponentRepo componentRepo;
	@Autowired
	private ItemRepo itemRepo;

	public ProductionRest(ProductionRepo productionRepo) {
		this.productionRepo = productionRepo;
	}

	@GetMapping("/production")
	Collection<Production> getAll() {
		return productionRepo.findAll();
	}

	@GetMapping("/production/{id}")
	ResponseEntity<Production> get(@PathVariable Long id) {
		Optional<Production> result = productionRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/production/tree/date/{date}")
	Collection<LineTreeDto> getTreeList(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		List<LineTreeDto> lines = new ArrayList<LineTreeDto>();
		List<ScheduleEvent> events = scheduleEventRepo.findByDate(date);
		for(ScheduleEvent se : events) {
			LineTreeDto line = lines.stream().filter(existingLine -> existingLine.getName().equals(se.getLine().getName())).findAny().orElse(null);
			if(line==null) {
				line = new LineTreeDto();
				line.setName(se.getLine().getName());
			}
			ItemTreeDto item = line.getItems().stream().filter(existingItem -> existingItem.getName().equals(se.getSaleItem().getItem().getName())).findAny().orElse(null);
			if(item==null) {
				item = new ItemTreeDto();
				item.setName(se.getSaleItem().getItem().getName());
			}
		}
		return lines;
	}

	// Save and update.
	@PostMapping("/production")
	ResponseEntity<Production> post(@RequestBody Production production) {
		if (production == null) {
			production = new Production();
		}
		Long existingUnitsProduced = 0L;
		if(production.getId() !=null) {
			Production existingProduction = productionRepo.getOne(production.getId());
			existingUnitsProduced = existingProduction.getUnitsProduced();
		}
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(production.getScheduleEvent().getId());
		Production result = productionRepo.save(production);
		Item item = itemRepo.findById(scheduleEvent.getSaleItem().getItem().getId()).get();
		item.addUnitsOnStack(result.getUnitsProduced() - existingUnitsProduced);
		item.addUnitsScheduled((result.getUnitsProduced() - existingUnitsProduced )* (-1));
		itemRepo.save(item);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/production/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Production existingProduction = productionRepo.getOne(id);
		Long existingUnitsProduced = existingProduction.getUnitsProduced();
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(existingProduction.getScheduleEvent().getId());
		Item item = itemRepo.findById(scheduleEvent.getSaleItem().getItem().getId()).get();
		item.addUnitsOnStack(existingUnitsProduced * (-1));
		productionRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}