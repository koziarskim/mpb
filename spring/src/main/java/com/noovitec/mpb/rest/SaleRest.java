package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
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
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;

@RestController
@RequestMapping("/api")
class SaleRest {

	private final Logger log = LoggerFactory.getLogger(SaleRest.class);
	private SaleRepo saleRepo;

	@Autowired
	ScheduleEventRepo scheduleEventRpo;
	
	public SaleRest(SaleRepo saleRepo) {
		this.saleRepo = saleRepo;
	}

	@GetMapping("/sale")
	Collection<Sale> getAll() {
		return saleRepo.findAll();
	}
	
	@GetMapping("/sale/pageable")
	Page<SaleListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey, @RequestParam(name = "searchType", required = false) String searchType) {
		Page<Sale> sales = null;
		if(searchType==null || searchType.isBlank() || searchKey==null || searchKey.isBlank()) {
			sales = saleRepo.findPage(pageable);
		}else if(searchType.equals("sale") && !searchKey.isBlank()) {
			sales = saleRepo.findPageBySale(pageable, searchKey);
		}else if(searchType.equals("item") && !searchKey.isBlank()){
			sales = saleRepo.findPageByItem(pageable, searchKey);
		}
		if(sales == null) {
			 return Page.empty();
		}
		Page<SaleListDto> all = sales.map(sale -> {
			SaleListDto dto = new SaleListDto();
			dto.setId(sale.getId());
			dto.setNumber(sale.getNumber());
			dto.setName(sale.getName());
			dto.setDc(sale.getShippingAddress()==null?"":sale.getShippingAddress().getDc());
			dto.setDate(sale.getDate());
			dto.setCustomerName(sale.getCustomer()==null?"":sale.getCustomer().getName());
			dto.setUnitsSold(sale.getUnitsSold());
			dto.setUnitsScheduled(sale.getUnitsScheduled());
			dto.setUnitsProduced(sale.getUnitsProduced());
		    return dto;
		});
		return all;
	}

	@GetMapping("/kv/sale/customer/{customer_id}")
	Collection<KeyValueDto> getAvailableFoSchedule(@PathVariable Long customer_id) {
		return saleRepo.findSalesForCustomer(customer_id);
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

	@PostMapping("/sale")
	ResponseEntity<Sale> post(@RequestBody(required = false) Sale sale) throws URISyntaxException {
		if (sale == null) {
			sale = new Sale();
		}
		// Needed for update.
		for (SaleItem sa : sale.getSaleItems()) {
			sa.setSale(sale);
		}
		Sale result = saleRepo.save(sale);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/sale/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Sale sale = saleRepo.getOne(id);
		for(SaleItem si: sale.getSaleItems()) {
			for(ScheduleEvent se: si.getScheduleEvents()) {
				scheduleEventRpo.deleteById(se.getId());
			}
		}
		saleRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}