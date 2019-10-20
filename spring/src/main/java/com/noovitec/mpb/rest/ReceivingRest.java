package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
	List<Receiving> getAll(@RequestParam(name = "purchase_id", required = false) Long purchase_id,
			@RequestParam(name = "component_id", required = false) Long component_id) {
		List<Receiving> receivings = new ArrayList<Receiving>();
		if(purchase_id != null && component_id !=null) {
			receivings = receivingRepo.findByPurchaseAndComponent(purchase_id, component_id);
		}else if(purchase_id != null && component_id == null) {
			receivings = receivingRepo.findByPurchase(purchase_id);
		}else if(purchase_id == null && component_id !=null) {
			receivings = receivingRepo.findByComponent(component_id);
		}else {
			receivings = receivingRepo.findAll();
		}
		return receivings;
	}

	@GetMapping("/receiving/{id}")
	ResponseEntity<Receiving> get(@PathVariable Long id) {
		Optional<Receiving> result = receivingRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	//TODO: Is this used?
	@GetMapping("/receiving/date/{date}")
	Map get(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		Collection<Receiving> receivings = receivingRepo.findReceivingInTransit();
		for(Receiving r : receivings) {
			Long c_id = r.getPurchaseComponent().getComponent().getId();
			int units = r.getUnits();
		}
		
		Map<LocalDate, Map> result = new HashMap<LocalDate, Map>();
		Map<String, Map> dateMap = new HashMap<String, Map>();
		Map<String, Integer> compMap = new HashMap<String, Integer>();
		compMap.put("feature", 1);
		compMap.put("past", 33);
		compMap.put("stack", 0);
		Map<String, Integer> compMap2 = new HashMap<String, Integer>();
		compMap2.put("feature", 2);
		compMap2.put("past", 33);
		compMap2.put("stack", 0);
		dateMap.put("C1", compMap);
		dateMap.put("C2", compMap2);
		LocalDate d1 = LocalDate.parse("27/04/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate d2 = LocalDate.parse("26/04/2019", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		result.put(d1, dateMap);
		result.put(d2, dateMap);
		return result;
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
			if (receiving.getReceivedDate()!=null) {
				component.addUnitsOnStack(Long.valueOf(receiving.getUnits()));
			}
			componentRepo.save(component);
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/receiving/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Receiving existingReceiving = receivingRepo.findById(id).get();
		if (existingReceiving.getPurchaseComponent()!=null && existingReceiving.getPurchaseComponent().getComponent() != null) {
			int unitsOnStack = existingReceiving.getPurchaseComponent().getComponent().getUnitsOnStack() - existingReceiving.getUnits();
			existingReceiving.getPurchaseComponent().getComponent().setUnitsOnStack(unitsOnStack);
		}
		receivingRepo.save(existingReceiving);
		Receiving receiving = receivingRepo.findById(id).get();
		receivingRepo.delete(receiving);
		return ResponseEntity.ok().build();
	}
}