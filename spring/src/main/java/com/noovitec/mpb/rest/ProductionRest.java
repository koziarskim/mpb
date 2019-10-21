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

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.ScheduleEvent;
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
		item.addUnitsOnStock(result.getUnitsProduced() - existingUnitsProduced);
		itemRepo.save(item);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/production/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Production existingProduction = productionRepo.getOne(id);
		Long existingUnitsProduced = existingProduction.getUnitsProduced();
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(existingProduction.getScheduleEvent().getId());
		Item item = itemRepo.findById(scheduleEvent.getSaleItem().getItem().getId()).get();
		item.addUnitsOnStock(existingUnitsProduced * (-1));
		productionRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}