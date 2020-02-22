package com.noovitec.mpb.rest;

import java.util.Arrays;
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

import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.repo.ProductionRepo;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.ProductionService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class ProductionRest {

	private final Logger log = LoggerFactory.getLogger(ProductionRest.class);
	private ProductionRepo productionRepo;
	@Autowired
	private ProductionService productionService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SaleService saleService;
	@Autowired
	private ComponentService componentService;

	ProductionRest(ProductionRepo productionRepo) {
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

	@PostMapping("/production")
	ResponseEntity<Production> post(@RequestBody Production production) {
		Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
		componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff);
		production = productionService.save(production);
		Long itemId = production.getScheduleEvent().getSaleItem().getItem().getId();
		Long saleId = production.getScheduleEvent().getSaleItem().getSale().getId();
		itemService.updateUnits(Arrays.asList(itemId));
		saleService.updateUnits(Arrays.asList(saleId));
		return ResponseEntity.ok().body(production);
	}

	@DeleteMapping("/production/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		Production production = productionRepo.getOne(id);
		Long unitsDiff = production.getUnitsProduced() * (-1);
		componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff);
		Long itemId = production.getScheduleEvent().getSaleItem().getItem().getId();
		Long saleId = production.getScheduleEvent().getSaleItem().getSale().getId();
		productionService.delete(id);
		itemService.updateUnits(Arrays.asList(itemId));
		saleService.updateUnits(Arrays.asList(saleId));
		return ResponseEntity.ok().build();
	}
	
}