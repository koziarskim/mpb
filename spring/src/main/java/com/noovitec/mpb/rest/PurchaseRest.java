package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URISyntaxException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.repo.PurchaseRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class PurchaseRest {

	@Autowired
	ObjectMapper objectMapper;

	private final Logger log = LoggerFactory.getLogger(PurchaseRest.class);
	private PurchaseRepo purchaseRepo;

	public PurchaseRest(PurchaseRepo purchaseRepo) {
		this.purchaseRepo = purchaseRepo;
	}

	@GetMapping("/purchase")
	Collection<Purchase> getAll() {
		return purchaseRepo.findAll();
	}

	@GetMapping("/purchase/{id}")
	ResponseEntity<Purchase> get(@PathVariable Long id) {
		Optional<Purchase> result = purchaseRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/purchase")
	ResponseEntity<Purchase> post(@RequestBody(required = false) Purchase purchase)
			throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		if (purchase == null) {
			purchase = new Purchase();
		}
		Purchase result = purchaseRepo.save(purchase);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/purchase/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
//		Optional<Sale> sale = saleRepo.findById(id);
//		sale.get().getCustomer().getAddresses().remove(sale.get());
//		saleRepo.delete(sale.get());
		purchaseRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}