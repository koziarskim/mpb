package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SaleItemRepo;

@RestController
@RequestMapping("/api")
class SaleItemRest {

	private final Logger log = LoggerFactory.getLogger(SaleItemRest.class);
	private SaleItemRepo saleItemRepo;

	public SaleItemRest(SaleItemRepo saleItemRepo) {
		this.saleItemRepo = saleItemRepo;
	}
	
	@GetMapping("/saleItem")
	List<SaleItem> getAll(@RequestParam(name = "ids", required = false) Long[] ids) {
		return  saleItemRepo.findAllByIds(ids);
	}
	
	@GetMapping("/saleItem/pageable")
	Page<SaleItemDto> getAllPageable(
			@RequestParam Pageable pageable, 
			@RequestParam(name = "numberName", required = false) String numberName, 
			@RequestParam(name = "itemId", required = false) Long itemId,
			@RequestParam(name = "customerId", required = false) Long customerId) {
		List<Long> ids = saleItemRepo.findIds(numberName, customerId, itemId);
		if(ids.isEmpty()) {
			return Page.empty();
		}
		Page<SaleItem> saleItems = saleItemRepo.findPage(pageable, ids);
		Page<SaleItemDto> all = saleItems.map(saleItem -> {
			SaleItemDto dto = new SaleItemDto();
			dto.setId(saleItem.getId());
			dto.setSaleId(saleItem.getSale().getId());
			dto.setSaleNumber(saleItem.getSale().getNumber());
			dto.setSaleName(saleItem.getSale().getName());
			dto.setItemId(saleItem.getItem().getId());
			dto.setItemNumber(saleItem.getItem().getNumber());
			dto.setItemName(saleItem.getItem().getName());
			dto.setCustomerId(saleItem.getSale().getCustomer().getId());
			dto.setCustomerName(saleItem.getSale().getCustomer().getName());
			dto.setUnitsSold(Long.valueOf(saleItem.getUnits()));
			dto.setUnitsProduced(saleItem.getUnitsProduced());
			dto.setUnitsShipped(saleItem.getUnitsShipped());
			dto.setUnitsOnStock(saleItem.getUnitsOnStock());
		    return dto;
		});
		return all;
	}

	@GetMapping("/saleItem/{id}")
	ResponseEntity<SaleItem> getSaleItem(@PathVariable Long id) {
		Optional<SaleItem> result = saleItemRepo.getSaleItemById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/saleItem/kv/customer/{customerId}")
	List<KeyValueDto> findKvByCustomer(@PathVariable Long customerId) {
		List<KeyValueDto> dtos = saleItemRepo.findKvByCustomer(customerId);
		return dtos;
	}

	@GetMapping("/saleItem/sale/{sale_id}")
	Collection<KeyValueDto> getAllByItem(@PathVariable Long sale_id) {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findSaleItemsBySale(sale_id);
		return saleDtos;
	}

	@GetMapping("/saleItem/kv")
	Collection<KeyValueDto> getAllKvs() {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findAllKvs();
		return saleDtos;
	}

}