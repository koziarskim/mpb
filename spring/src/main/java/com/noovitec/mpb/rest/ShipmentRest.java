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

import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.ShipmentRepo;

@RestController
@RequestMapping("/api")
class ShipmentRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentRest.class);
	private ShipmentRepo shipmentRepo;

	public ShipmentRest(ShipmentRepo shipmentRepo) {
		this.shipmentRepo = shipmentRepo;
	}

	@GetMapping("/shipment")
	Collection<Shipment> getAll() {
		return shipmentRepo.findAll();
	}

	@GetMapping("/shipment/{id}")
	ResponseEntity<Shipment> get(@PathVariable Long id) {
		Optional<Shipment> result = shipmentRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/shipment")
	ResponseEntity<Shipment> post(@RequestBody(required = false) Shipment shipment) throws URISyntaxException {
		if (shipment == null) {
			shipment = new Shipment();
		}
		Shipment result = shipmentRepo.save(shipment);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/shipment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		shipmentRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}