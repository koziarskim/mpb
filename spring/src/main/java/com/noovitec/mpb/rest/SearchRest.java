package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SearchDto;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SearchRepo;

@RestController
@RequestMapping("/api")
class SearchRest {

	private final Logger log = LoggerFactory.getLogger(SearchRest.class);
	private SearchRepo searchRepo;

	public SearchRest(SearchRepo searchRepo) {
		this.searchRepo = searchRepo;
	}

	@PostMapping("/search/season/kv")
	List<KeyValueDto> getAllSeasons(@RequestBody SearchDto searchDto) {
		List<KeyValueDto> results = searchRepo.findSeasons(searchDto);
		return results;
	}

	@PostMapping("/search/customer/kv")
	List<KeyValueDto> getAllCustomers(@RequestBody SearchDto searchDto) {
		return searchRepo.findCustomers(searchDto);
	}

	@PostMapping("/search/item/kv")
	List<KeyValueDto> getAllItems(@RequestBody SearchDto searchDto) {
		return searchRepo.findItems(searchDto);
	}

	@PostMapping("/search/sale/kv")
	List<KeyValueDto> getAllSales(@RequestBody SearchDto searchDto) {
		List<SaleItem> sales = searchRepo.findSales(searchDto);
		List<KeyValueDto> dtos = new ArrayList<KeyValueDto>();
		for(SaleItem si: sales) {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(si.getId());
			dto.setName(si.getSale().getNumber());
			dtos.add(dto);
		}
		return dtos;
	}

	@PostMapping("/search/supplier/kv")
	List<KeyValueDto> getAllSuppliers(@RequestBody SearchDto searchDto) {
		return searchRepo.findSuppliers(searchDto);
	}

	@PostMapping("/search/component/kv")
	List<KeyValueDto> getAllComponents(@RequestBody SearchDto searchDto) {
		List<Component> components = searchRepo.findComponents(searchDto);
		List<KeyValueDto> dtos = new ArrayList<KeyValueDto>();
		for(Component c: components) {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(c.getId());
			dto.setName(c.getName());
			dtos.add(dto);
		}
		return dtos;
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