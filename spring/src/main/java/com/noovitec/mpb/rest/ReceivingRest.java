package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.noovitec.mpb.dto.ReceivingListDto;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.ReceivingRepo;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.NotificationService;
import com.noovitec.mpb.service.PurchaseService;
import com.noovitec.mpb.service.ReceivingService;

@RestController
@RequestMapping("/api")
class ReceivingRest {

	private final Logger log = LoggerFactory.getLogger(ReceivingRest.class);
	private ReceivingRepo receivingRepo;
	@Autowired
	private PurchaseRepo purchaseRepo;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private ReceivingService receivingService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private CrudService crudService;
	@Autowired
	private NotificationService notificationService;
	
	public ReceivingRest(ReceivingRepo receivingRepo) {
		this.receivingRepo = receivingRepo;
	}

	@GetMapping("/receiving")
	List<Receiving> getAll() {
		return  receivingRepo.findAll();
	}

	@GetMapping("/receiving/pageable")
	Page<ReceivingListDto> getAllPageable(@RequestParam(required = false) Pageable pageable, 
			@RequestParam(required = false) Long purchaseId,
			@RequestParam(required = false) Long componentId,
			@RequestParam(required = false) Long supplierId,
			@RequestParam(required = false) String invoiceNumber) {
		Page<Receiving> receivings = receivingRepo.findPagable(pageable, purchaseId, componentId, supplierId, invoiceNumber);
		Page<ReceivingListDto> dtos = receivings.map(receiving -> {
			ReceivingListDto dto = new ReceivingListDto();
			dto.setId(receiving.getId());
			dto.setNumber(receiving.getNumber());
			dto.setName(receiving.getName());
			dto.setPurchaseId(receiving.getPurchaseComponent().getPurchase().getId());
			dto.setPurchaseNumber(receiving.getPurchaseComponent().getPurchase().getNumber());
			dto.setPurchaseName(receiving.getPurchaseComponent().getPurchase().getName());
			dto.setComponentId(receiving.getPurchaseComponent().getComponent().getId());
			dto.setComponentNumber(receiving.getPurchaseComponent().getComponent().getNumber());
			dto.setComponentName(receiving.getPurchaseComponent().getComponent().getName());
			dto.setSupplierName(receiving.getPurchaseComponent().getPurchase().getSupplier().getName());
			dto.setInvoiceNumber(receiving.getInvoiceNumber());
			dto.setContainerNumber(receiving.getContainerNumber());
			dto.setReceivedDate(receiving.getReceivingDate());
			dto.setUnitsReceived(receiving.getUnits());
			dto.setUnitPrice(receiving.getUnitPrice());
			return dto;
		});
		return dtos;
	}

	@GetMapping("/receiving/{id}")
	ResponseEntity<Receiving> get(@PathVariable Long id) {
		Optional<Receiving> result = receivingRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/receivings/purchase/{purchase_id}")
	ResponseEntity<?> postPurchase(@PathVariable Long purchase_id, @RequestBody Receiving[] receivings) throws URISyntaxException {
		for(Receiving receiving: receivings) {
			receiving = this.receive(receiving);
		}
		purchaseRepo.updateReceivingDate(receivings[0].getReceivingDate(), purchase_id);
		purchaseService.updateUnits(Arrays.asList(purchase_id));
		Purchase purchase = purchaseRepo.getOne(purchase_id);
		return ResponseEntity.ok().body(purchase);
	}

	@PostMapping("/receiving")
	ResponseEntity<?> post(@RequestBody Receiving receiving) {
		if(receiving.getId()==null && receiving.getPurchaseComponent().getComponent().getCategory().getName().equalsIgnoreCase("Food")) {
			List<String> emails = new ArrayList<String>();
			emails.add("hpyzikiewicz@marketplacebrands.com");
			Map<String, String> model = new HashMap<String, String>();
			String componentNumber = receiving.getPurchaseComponent().getComponent().getNumber() + " "+receiving.getPurchaseComponent().getComponent().getName();
			model.put("componentNumber", componentNumber);
			notificationService.sendMail(emails, model, Notification.TYPE.COMPONENT_RECEIVED);
		}
		receiving = this.receive(receiving);
		return ResponseEntity.ok().body(receiving);
	}

	private Receiving receive(Receiving receiving) {
		receiving = receivingService.save(receiving);
		//TODO: There is something strang with timing that receivng is not added to list.
		boolean exists = false;
		for(Receiving r: receiving.getPurchaseComponent().getReceivings()) {
			if(r.getId() == receiving.getId()) {
				exists = true;
			}
		}
		if(!exists) {
			receiving.getPurchaseComponent().getReceivings().add(receiving);
		}
		purchaseService.updateUnits(Arrays.asList(receiving.getPurchaseComponent().getPurchase().getId()));
		List<Long> itemIds = itemService.findIdsByComponents(Arrays.asList(receiving.getPurchaseComponent().getComponent().getId()));
		itemService.updateUnits(itemIds);
		componentService.updateUnits(Arrays.asList(receiving.getPurchaseComponent().getComponent().getId()));
		itemService.updateUnitsReadyProd(itemIds);
		return receiving;
	}

	@DeleteMapping("/receiving/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Receiving receiving = receivingRepo.getOne(id);
		Long purchaseId = receiving.getPurchaseComponent().getPurchase().getId();
		Long componentId = receiving.getPurchaseComponent().getComponent().getId();
		List<Long> itemIds = new ArrayList<Long>();
		receiving.getPurchaseComponent().getComponent().getItemComponents().forEach(ic -> {
			itemIds.add(ic.getItem().getId());
		});
		receivingService.delete(id);
		itemService.updateUnits(itemIds);
		componentService.updateUnits(Arrays.asList(componentId));
		itemService.updateUnitsReadyProd(itemIds);
		purchaseService.updateUnits(Arrays.asList(purchaseId));
		return ResponseEntity.ok().build();
	}
}