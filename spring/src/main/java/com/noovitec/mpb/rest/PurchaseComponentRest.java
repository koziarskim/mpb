package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.PurchaseComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.ReceivingRepo;


@RestController
@RequestMapping("/api")
class PurchaseComponentRest {

	private final Logger log = LoggerFactory.getLogger(PurchaseComponentRest.class);
	PurchaseComponentRepo purchaseComponentRepo;

	public PurchaseComponentRest(PurchaseComponentRepo purchaseComponentRepo) {
		this.purchaseComponentRepo = purchaseComponentRepo;
	}

	@GetMapping("/purchaseComponent")
	Collection<PurchaseComponent> getAll() {
		return purchaseComponentRepo.findAll();
	}

	@GetMapping("/purchaseComponent/purchase/{purchase_id}")
	Collection<PurchaseComponent> getByPurchase(@PathVariable Long purchase_id) {
		Collection<PurchaseComponent> result = purchaseComponentRepo.findByPurchase(purchase_id);
		return result;
	}

	@GetMapping("/purchaseComponent/component/{component_id}")
	Collection<PurchaseComponent> getByComponent(@PathVariable Long component_id) {
		Collection<PurchaseComponent> result = purchaseComponentRepo.findByComponent(component_id);
		return result;
	}

	@PostMapping("/purchaseComponent")
	ResponseEntity<?> post(@RequestBody(required = false) PurchaseComponent purchaseComponent) throws URISyntaxException {
		PurchaseComponent result = purchaseComponentRepo.save(purchaseComponent);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/purchaseComponent/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		PurchaseComponent purchaseComponent = purchaseComponentRepo.findById(id).get();
		purchaseComponentRepo.delete(purchaseComponent);
		return ResponseEntity.ok().build();
	}
}