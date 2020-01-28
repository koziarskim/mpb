package com.noovitec.mpb.rest;

import java.util.ArrayList;
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
	Page<SaleListDto> getAllPageable(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required = false) String searchKey,
			@RequestParam(required = false) String searchType,
			@RequestParam(required = false) boolean hideProd,
			@RequestParam(required = false) boolean hideShip) {
		Page<Sale> sales = saleRepo.findPagable(pageable, searchKey, searchType, hideProd, hideShip);
		Page<SaleListDto> all = sales.map(sale -> {
			SaleListDto dto = new SaleListDto();
			dto.setId(sale.getId());
			dto.setNumber(sale.getNumber());
			dto.setName(sale.getName());
			dto.setDc(sale.getShippingAddress() == null ? "" : sale.getShippingAddress().getDc()+" ("+sale.getShippingAddress().getState()+")");
			dto.setDate(sale.getDate());
			dto.setCustomerName(sale.getCustomer() == null ? "" : sale.getCustomer().getName());
			dto.setUnitsSold(sale.getUnitsSold());
			dto.setUnitsScheduled(sale.getUnitsScheduled());
			dto.setUnitsProduced(sale.getUnitsProduced());
			dto.setUnitsTransfered(sale.getUnitsTransfered());
			dto.setUnitsShipped(sale.getUnitsShipped());
			return dto;
		});
		return all;
	}

	@GetMapping("/kv/sale/customer/{customer_id}")
	Collection<KeyValueDto> getAvailableFoSchedule(@PathVariable Long customer_id) {
		return saleRepo.findSalesForCustomer(customer_id);
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

	// This is acting as POST.
	@GetMapping("/sale/update/units")
	ResponseEntity<?> postUpdateUnits() {
		try {
			saleService.updateUnits();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping("/sale/units/{saleId}")
	ResponseEntity<?> updateUnits(@PathVariable Long saleId) {
		Sale sale = saleRepo.getOne(saleId);
		sale.updateUnits();
		saleRepo.save(sale);
		return ResponseEntity.ok().body("OK");
	}
	
	@PostMapping("/sale")
	ResponseEntity<Sale> post(@RequestBody(required = false) Sale sale) {
		if (sale == null) {
			sale = new Sale();
		}
		for (SaleItem sa : sale.getSaleItems()) {
			sa.setSale(sale);
			for(SaleItemTransfer t: sa.getTransfersTo()) {
				if(t.getSaleItemTo()==null) {
					SaleItem saTo = new SaleItem();
					saTo.setId(t.getSaleItemToId());
					t.setSaleItemTo(saTo);
				}
				if(t.getSaleItemFrom()==null) {
					SaleItem saFrom = new SaleItem();
					saFrom.setId(t.getSaleItemFromId());
					t.setSaleItemFrom(saFrom);
				}
			}
			for(SaleItemTransfer t: sa.getTransfersFrom()) {
				if(t.getSaleItemTo()==null) {
					SaleItem saTo = new SaleItem();
					saTo.setId(t.getSaleItemToId());
					t.setSaleItemTo(saTo);
				}
				if(t.getSaleItemFrom()==null) {
					SaleItem saFrom = new SaleItem();
					saFrom.setId(t.getSaleItemFromId());
					t.setSaleItemFrom(saFrom);
				}
			}
		}
		sale = (Sale) crudService.merge(sale);
		sale.updateUnits();
		for (SaleItem sa : sale.getSaleItems()) {
			sa.getItem().updateUnits();
		}
		Sale result = (Sale) crudService.save(sale);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/sale/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		// TODO: Is there a better way of doing it?
		List<Item> items = new ArrayList<Item>();
		Sale sale = saleRepo.getOne(id);
		for (SaleItem sa : sale.getSaleItems()) {
			items.add(sa.getItem());
		}
		saleRepo.deleteById(id);
		for (Item item : items) {
			item.updateUnits();
			crudService.save(item);
		}
		return ResponseEntity.ok().build();
	}
}