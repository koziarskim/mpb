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

import com.noovitec.mpb.entity.ProductionOutput;
import com.noovitec.mpb.repo.ProductionOutputRepo;

@RestController
@RequestMapping("/api")
class ProductionOutputRest {

	private final Logger log = LoggerFactory.getLogger(ProductionOutputRest.class);
	private ProductionOutputRepo productionOutputRepo;

	public ProductionOutputRest(ProductionOutputRepo productionOutputRepo) {
		this.productionOutputRepo = productionOutputRepo;
	}

	@GetMapping("/productionOutput")
	Collection<ProductionOutput> getAll() {
		return productionOutputRepo.findAll();
	}

	@GetMapping("/productionOutput/{id}")
	ResponseEntity<ProductionOutput> get(@PathVariable Long id) {
		Optional<ProductionOutput> result = productionOutputRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Save and update.
	@PostMapping("/productionOutput")
	ResponseEntity<ProductionOutput> post(@RequestBody ProductionOutput production) {
		if (production == null) {
			production = new ProductionOutput();
		}
		ProductionOutput result = productionOutputRepo.save(production);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/productionOutput/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		productionOutputRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}