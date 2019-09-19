package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Converter;
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

import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleDto;
import com.noovitec.mpb.dto.SaleListDto;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SaleRepo;

@RestController
@RequestMapping("/api")
class SaleRest {

	private final Logger log = LoggerFactory.getLogger(SaleRest.class);
	private SaleRepo saleRepo;

	public SaleRest(SaleRepo saleRepo) {
		this.saleRepo = saleRepo;
	}

	@GetMapping("/sale")
	Collection<Sale> getAll() {
		return saleRepo.findAll();
	}
	
	@GetMapping("/sale/pageable")
	Page<SaleListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		Page<Sale> sales = null;
		if(searchKey ==null || searchKey.trim().length() == 0) {
			sales = saleRepo.getSalePageable(pageable);
		}else {
			sales = saleRepo.getSalePageable(pageable, searchKey);
		}
		Page<SaleListDto> all = sales.map(sale -> {
			SaleListDto dto = new SaleListDto();
			dto.setId(sale.getId());
			dto.setNumber(sale.getNumber());
			dto.setCustomerName(sale.getCustomer().getName());
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

	@GetMapping("/sale/purchase/{purchase_id}")
	Collection<SaleDto> getAllByPurchase(@PathVariable Long purchase_id) {
		Collection<SaleDto> saleDtos = saleRepo.findAllSalesAndPurchaseSales(purchase_id);
		return saleDtos;
	}

	@GetMapping("/sale/customer/{customer_id}")
	Collection<Sale> getAllByCustomer(@PathVariable Long customer_id) {
		Collection<Sale> sales = saleRepo.findSaleByCustomer(customer_id);
		return sales;
	}

	@GetMapping("/saleItem/{id}")
	ResponseEntity<SaleItem> getSaleItem(@PathVariable Long id) {
		Optional<SaleItem> result = saleRepo.getSaleItemById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/saleItem/sale/{sale_id}")
	Collection<KeyValueDto> getAllByItem(@PathVariable Long sale_id) {
		Collection<KeyValueDto> saleDtos = saleRepo.findSaleItemsBySale(sale_id);
		return saleDtos;
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
		saleRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}