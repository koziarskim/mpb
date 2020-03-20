package com.noovitec.mpb.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleListDto;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.SaleItemTransfer;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.CustomerService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class SaleRest {

	@Autowired
	private SaleRepo saleRepo;
	@Autowired
	ItemRepo itemRepo;
	@Autowired
	ScheduleEventRepo scheduleEventRpo;
	@Autowired
	CrudService crudService;
	@Autowired
	CustomerService customerService;
	
	private final Logger log = LoggerFactory.getLogger(SaleRest.class);
	private SaleService saleService;

	public SaleRest(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping("/sale")
	Collection<Sale> getAll() {
		return saleRepo.findAll();
	}

	@GetMapping("/sale/pageable")
	Page<SaleListDto> getAllPageable(
			@RequestParam(required = false) Pageable pageable, 
			@RequestParam(required = false) String saleNumber,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) Long customerId,
			@RequestParam(required = false) String status) {
		Page<Sale> sales = saleRepo.findPagable(pageable, saleNumber, itemId, customerId, status);
		Page<SaleListDto> all = sales.map(sale -> {
			SaleListDto dto = new SaleListDto();
			dto.setId(sale.getId());
			dto.setNumber(sale.getNumber());
			dto.setName(sale.getName());
			dto.setDc(sale.getShippingAddress() == null ? "" : sale.getShippingAddress().getDc() + " (" + sale.getShippingAddress().getState() + ")");
			dto.setDate(sale.getDate());
			dto.setCustomerName(sale.getCustomer() == null ? "" : sale.getCustomer().getName());
			dto.setUnitsSold(sale.getUnitsSold());
			dto.setUnitsScheduled(sale.getUnitsScheduled());
			dto.setUnitsProduced(sale.getUnitsProduced());
			dto.setUnitsTransferedTo(sale.getUnitsTransferedTo());
			dto.setUnitsTransferedFrom(sale.getUnitsTransferedFrom());
			dto.setUnitsShipped(sale.getUnitsShipped());
			dto.setUnitsOnStock(sale.getUnitsOnStock());
			dto.setUnitsAdjusted(sale.getUnitsAdjusted());
			dto.setStatus(sale.getStatus());
			return dto;
		});
		return all;
	}

	@GetMapping("/sale/kv/customer/{customer_id}")
	Collection<KeyValueDto> findKvByCustomer(
			@PathVariable Long customer_id,
			@RequestParam(required=false) boolean onlyStock) {
		List<KeyValueDto> dtos = onlyStock?saleRepo.findKvByCustomerAndStock(customer_id):saleRepo.findKvByCustomer(customer_id);
		return dtos;
	}

	@GetMapping("/sale/kv")
	Collection<KeyValueDto> getKvs() {
		return saleRepo.findKvs();
	}

	@GetMapping("/sale/{id}")
	ResponseEntity<Sale> get(@PathVariable Long id) {
		Optional<Sale> result = saleRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/sale/customer/{customer_id}")
	Collection<Sale> getAllByCustomer(@PathVariable Long customer_id) {
		Collection<Sale> sales = saleRepo.findSaleByCustomer(customer_id);
		return sales;
	}

	@GetMapping("/sale/saleItem/{saleItemId}")
	Sale getBySaleItem(@PathVariable Long saleItemId) {
		Sale sale = saleRepo.getBySaleItem(saleItemId);
		return sale;
	}

	@GetMapping("/sale/validate/number/{number}")
	ResponseEntity<?> validateNumber(@PathVariable String number) {
		Long id = saleRepo.getIdByNumber(number);
		if(id==null) {
			return ResponseEntity.ok().build();
		}else {
			return ResponseEntity.ok().body("Duplicate");
		}
	}

	@PostMapping("/sale/units/{saleId}")
	ResponseEntity<?> updateUnits(@PathVariable Long saleId) {
		Sale sale = saleRepo.getOne(saleId);
		sale.updateUnits();
		saleRepo.save(sale);
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping("/sale")
	ResponseEntity<?> post(@RequestBody Sale sale) {
		if(!sale.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = saleRepo.getIdByNumber(sale.getNumber());
		if((sale.getId()==null && id !=null) || (sale.getId()!=null && id !=null && !sale.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale Number already exists. Please, choose differrent.");
		}
		for (SaleItem sa : sale.getSaleItems()) {
			sa.setSale(sale);
			for (SaleItemTransfer t : sa.getTransfersTo()) {
				if (t.getSaleItemTo() == null) {
					SaleItem saTo = new SaleItem();
					saTo.setId(t.getSaleItemToId());
					t.setSaleItemTo(saTo);
				}
				if (t.getSaleItemFrom() == null) {
					SaleItem saFrom = new SaleItem();
					saFrom.setId(t.getSaleItemFromId());
					t.setSaleItemFrom(saFrom);
				}
			}
			for (SaleItemTransfer t : sa.getTransfersFrom()) {
				if (t.getSaleItemTo() == null) {
					SaleItem saTo = new SaleItem();
					saTo.setId(t.getSaleItemToId());
					t.setSaleItemTo(saTo);
				}
				if (t.getSaleItemFrom() == null) {
					SaleItem saFrom = new SaleItem();
					saFrom.setId(t.getSaleItemFromId());
					t.setSaleItemFrom(saFrom);
				}
			}
		}
		sale = (Sale) crudService.merge(sale);
		sale.setModifiedDate(LocalDateTime.now());
		sale.updateUnits();
		for (SaleItem sa : sale.getSaleItems()) {
			sa.getItem().updateUnits();
		}
		customerService.updateUnits(Arrays.asList(sale.getCustomer().getId()));
		Sale result = (Sale) crudService.save(sale);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/sale/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Sale sale = saleRepo.getOne(id);
		if(sale.getSaleItems().size()>0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Sale Items!");
		}
		List<Item> items = new ArrayList<Item>();
		Long customerId = sale.getCustomer().getId();
		for (SaleItem sa : sale.getSaleItems()) {
			items.add(sa.getItem());
		}
		saleRepo.deleteById(id);
		for (Item item : items) {
			item.updateUnits();
			crudService.save(item);
		}
		customerService.updateUnits(Arrays.asList(customerId));
		return ResponseEntity.ok().build();
	}
	
	// This is acting as POST.
	@GetMapping("/sale/update/units")
	ResponseEntity<?> postUpdateUnits() {
		try {
			saleService.updateUnits(null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}
	
	// This is acting as POST.
	@GetMapping("/sale/update/number")
	ResponseEntity<?> postUpdateNumber() {
		try {
			saleService.updateNumber();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}

	// This is acting as POST.
	@GetMapping("/sale/update/merge")
	ResponseEntity<?> mergeSales() {
		try {
			saleService.mergeSales();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}

}