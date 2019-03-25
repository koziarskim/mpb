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

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ReceivingRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ReceivingRest {

	private final Logger log = LoggerFactory.getLogger(ReceivingRest.class);
	private ReceivingRepo receivingRepo;
	@Autowired
	ComponentRepo componentRepo;

	public ReceivingRest(ReceivingRepo receivingRepo) {
		this.receivingRepo = receivingRepo;
	}

	@GetMapping("/receiving")
	Collection<Receiving> getAll(@RequestParam(name = "purchase_id", required = false) Long purchase_id,
			@RequestParam(name = "component_id", required = false) Long component_id) {
		Collection<Receiving> result = new HashSet<Receiving>();
		for (Receiving receiving : receivingRepo.findAll()) {
			boolean foundPurchase = false;
			if (purchase_id == null) {
				foundPurchase = true;
			} else if (receiving.getPurchase() != null && purchase_id.equals(receiving.getPurchase().getId())) {
				foundPurchase = true;
			}
			boolean foundComponent = false;
			if (component_id == null) {
				foundComponent = true;
			} else if (receiving.getComponent() != null && component_id.equals(receiving.getComponent().getId())) {
				foundComponent = true;
			}
			if (foundPurchase && foundComponent) {
				result.add(receiving);
			}
		}
		return result;
	}

	@GetMapping("/receiving/{id}")
	ResponseEntity<Receiving> get(@PathVariable Long id) {
		Optional<Receiving> result = receivingRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/receiving")
	ResponseEntity<Receiving> post(@RequestBody(required = false) Receiving receiving) throws URISyntaxException {
		if (receiving == null) {
			receiving = new Receiving();
		}
//		 TODO: Need to check if update.
		if (receiving.getComponent() != null) {
//			Receiving existingReceiving = receivingRepo.getOne(receiving.getId());
			int unitsOnStack = receiving.getComponent().getUnitsOnStack() + receiving.getUnits();
			receiving.getComponent().setUnitsOnStack(unitsOnStack);
		}
		Receiving result = receivingRepo.save(receiving);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/receiving/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Receiving existingReceiving = receivingRepo.findById(id).get();
		if (existingReceiving.getComponent() != null) {
			int unitsOnStack = existingReceiving.getComponent().getUnitsOnStack() - existingReceiving.getUnits();
			existingReceiving.getComponent().setUnitsOnStack(unitsOnStack);
		}
		receivingRepo.save(existingReceiving);
		Receiving receiving = receivingRepo.findById(id).get();
		receivingRepo.delete(receiving);
		return ResponseEntity.ok().build();
	}
}