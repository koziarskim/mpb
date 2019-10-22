package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.ReceivingRepo;


@RestController
@RequestMapping("/api")
class ReceivingRest {

	private final Logger log = LoggerFactory.getLogger(ReceivingRest.class);
	private ReceivingRepo receivingRepo;
	@Autowired
	PurchaseRepo purchaseRepo;
	@Autowired
	ComponentRepo componentRepo;

	public ReceivingRest(ReceivingRepo receivingRepo) {
		this.receivingRepo = receivingRepo;
	}

	@GetMapping("/receiving")
	List<Receiving> getAll() {
		return  receivingRepo.findAll();
	}

	@GetMapping("/receiving/pageable")
	Page<Receiving> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, 
			@RequestParam(name = "purchase_id", required = false) Long purchase_id,
			@RequestParam(name = "component_id", required = false) Long component_id) {
		Page<Receiving> receivings = null;
		if(purchase_id != null && component_id !=null) {
			receivings = receivingRepo.findByPurchaseAndComponent(pageable, purchase_id, component_id);
		}else if(purchase_id != null && component_id == null) {
			receivings = receivingRepo.findByPurchase(pageable, purchase_id);
		}else if(purchase_id == null && component_id !=null) {
			receivings = receivingRepo.findByComponent(pageable, component_id);
		}else {
			receivings = receivingRepo.findPage(pageable);
		}
		if(receivings == null) {
			receivings = Page.empty();
		}
		return receivings;
	}

	@GetMapping("/receiving/{id}")
	ResponseEntity<Receiving> get(@PathVariable Long id) {
		Optional<Receiving> result = receivingRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/receivings/purchase/{purchase_id}")
	ResponseEntity<?> postPurchase(@PathVariable Long purchase_id, @RequestBody Receiving[] receivings) throws URISyntaxException {
		for(Receiving receiving: receivings) {
			this.post(receiving);
		}
		purchaseRepo.updateReceivingDate(receivings[0].getReceivingDate(), purchase_id);
		Purchase purchase = purchaseRepo.getOne(purchase_id);
		return ResponseEntity.ok().body(purchase);
	}

	@PostMapping("/receiving")
	ResponseEntity<?> post(@RequestBody(required = false) Receiving receiving) throws URISyntaxException {
		if (receiving == null) {
			receiving = new Receiving();
		}
		Receiving result = receivingRepo.save(receiving);
		result = receivingRepo.save(receiving);
		if(receiving.getPurchaseComponent()!=null && receiving.getPurchaseComponent().getComponent() != null) {
			Component component = componentRepo.findById(receiving.getPurchaseComponent().getComponent().getId()).get();
			if (receiving.getReceivingDate()!=null) {
				component.addUnitsOnStock(receiving.getUnits());
			}
			componentRepo.save(component);
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/receiving/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Receiving existingReceiving = receivingRepo.findById(id).get();
		if (existingReceiving.getReceivingDate()!=null) {
			Long unitsOnStock = existingReceiving.getPurchaseComponent().getComponent().getUnitsOnStock() - existingReceiving.getUnits();
			existingReceiving.getPurchaseComponent().getComponent().setUnitsOnStock(unitsOnStock);
		}
		receivingRepo.save(existingReceiving);
		Receiving receiving = receivingRepo.findById(id).get();
		receivingRepo.delete(receiving);
		return ResponseEntity.ok().build();
	}
}