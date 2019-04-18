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
import org.springframework.web.server.ResponseStatusException;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.ScheduleItem;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ProductionRepo;
import com.noovitec.mpb.repo.ScheduleItemRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ProductionRest {

	private final Logger log = LoggerFactory.getLogger(ProductionRest.class);
	private ProductionRepo productionRepo;
	@Autowired
	private ScheduleItemRepo scheduleItemRepo;
	@Autowired
	private ComponentRepo componentRepo;

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
		Production result = productionRepo.save(production);
		ScheduleItem scheduleItem = scheduleItemRepo.getOne(result.getScheduleItem().getId());
		//update component.unitsReserved then component.unitsOnStack
		for(ItemComponent ic : scheduleItem.getItem().getItemComponents()) {
			Long componentUnits = result.getUnitsProduced() * ic.getUnits();
			Component component = ic.getComponent();
			//Subtract
			Long extraUnits = component.addUnitsReserved(componentUnits * (-1));
			if(extraUnits > 0) {
				throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Units produced exceeded units reserved.");
			}
			//Subtract
			extraUnits = component.addUnitsOnStack(componentUnits * (-1));
			if(extraUnits > 0) {
				throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Units produced exceeded units on stack.");
			}
			componentRepo.save(component);
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/production/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		productionRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}