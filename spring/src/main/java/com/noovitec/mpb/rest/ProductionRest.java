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
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.repo.ProductionRepo;
import com.noovitec.mpb.service.CrudService;

@RestController
@RequestMapping("/api")
class ProductionRest {

	private final Logger log = LoggerFactory.getLogger(ProductionRest.class);
	
	private ProductionRepo productionRepo;
	
	@Autowired
	private CrudService crudService;

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
		production = (Production) crudService.merge(production);
		production.getScheduleEvent().getSaleItem().getItem().updateUnits();
		production.getScheduleEvent().getSaleItem().getSale().updateUnits();
		Production result = productionRepo.save(production);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/production/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		//TODO: Is there a better way of doing it?
		Production production = productionRepo.getOne(id);
		Item item = production.getScheduleEvent().getSaleItem().getItem();
		Sale sale = production.getScheduleEvent().getSaleItem().getSale();
		productionRepo.deleteById(id);
		item.updateUnits();
		sale.updateUnits();
		crudService.save(item);
		crudService.save(sale);
		return ResponseEntity.ok().build();
	}

}