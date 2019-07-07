package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.ProductionLine;
import com.noovitec.mpb.repo.ProductionLineRepo;

@RestController
@RequestMapping("/api")
class ProductionLineRest {

	private final Logger log = LoggerFactory.getLogger(ProductionLineRest.class);
	private ProductionLineRepo productionLineRepo;

	public ProductionLineRest(ProductionLineRepo productionLineRepo) {
		this.productionLineRepo = productionLineRepo;
	}

	@GetMapping("/productionLine")
	Collection<ProductionLine> getAll() {
		return productionLineRepo.findAll();
	}

	@GetMapping("/productionLine/{id}")
	ResponseEntity<ProductionLine> get(@PathVariable Long id) {
		Optional<ProductionLine> result = productionLineRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/productionLine")
	ResponseEntity<ProductionLine> post(@RequestBody ProductionLine productionLine) {
		if (productionLine == null) {
			productionLine = new ProductionLine();
		}
		ProductionLine result = productionLineRepo.save(productionLine);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/productionLine/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		productionLineRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}