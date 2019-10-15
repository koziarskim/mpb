package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.SearchRepo;

@RestController
@RequestMapping("/api")
class SearchRest {

	private final Logger log = LoggerFactory.getLogger(SearchRest.class);
	private SearchRepo searchRepo;

	public SearchRest(SearchRepo searchRepo) {
		this.searchRepo = searchRepo;
	}

	@GetMapping("/search/supplier/kv")
	Collection<KeyValueDto> getAllSuppliers(@RequestParam(name = "supplierName", required = false) String supplierName) {
		return searchRepo.findSuppliers(supplierName);
	}

	@GetMapping("/search/item/kv")
	Collection<KeyValueDto> getAllItems(@RequestParam(name = "itemName", required = false) String itemName, @RequestParam(name = "supplierId", required = false) Long supplierId) {
		return searchRepo.findItems(itemName, supplierId);
	}

	@GetMapping("/search/sale/kv")
	Collection<KeyValueDto> getAllSales(@RequestParam(name = "saleNumber", required = false) String saleNumber) {
		return searchRepo.findSales(saleNumber);
	}

	/*
	 * Use this to create and update entity. No need to use PUT for update. If ID is
	 * not null, it will try to update.
	 */
	@PostMapping("/search/item")
	ResponseEntity<Item> post(@RequestBody(required = false) Item jsonItem) throws URISyntaxException {
//		Item result = searchRepo.save(jsonItem);
		return ResponseEntity.ok().body(null);
	}
}