package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
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

import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.ShipmentItemRepo;

@RestController
@RequestMapping("/api")
class ShipmentItemRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentItemRest.class);
	private ShipmentItemRepo shipmentItemRepo;

	public ShipmentItemRest(ShipmentItemRepo shipmentItemRepo) {
		this.shipmentItemRepo = shipmentItemRepo;
	}

	@GetMapping("/shipmentItem")
	Collection<ShipmentItem> getAll() {
		return shipmentItemRepo.findAll();
	}

	@GetMapping("/shipmentItem/{id}")
	ResponseEntity<ShipmentItem> get(@PathVariable Long id) {
		Optional<ShipmentItem> result = shipmentItemRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/shipmentItem")
	ResponseEntity<ShipmentItem> post(@RequestBody(required = false) ShipmentItem shipmentItem) throws URISyntaxException {
		if (shipmentItem == null) {
			shipmentItem = new ShipmentItem();
		}
		ShipmentItem result = shipmentItemRepo.save(shipmentItem);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/shipmentItem/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		shipmentItemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}