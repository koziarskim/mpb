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
	
	@GetMapping("/saleItem/pageable")
	Page<SaleItemDto> getAllPageable(
			@RequestParam Pageable pageable, 
			@RequestParam(name = "numberName", required = false) String numberName, 
			@RequestParam(name = "itemId", required = false) Long itemId,
			@RequestParam(name = "customerId", required = false) Long customerId) {
		if(numberName != null && !numberName.isBlank()) {
			if(itemId != null) {
				if(customerId != null) {
					//TODO: Search by numberName && itemId && customerId
				}else {
					//TODO: Search by numberName && itemId
				}
			}else {
				if(customerId !=null) {
					//TODO: search by numberName && customerId
				}else {
					//TODO: search by numberName
				}
			}
		}else {
			if(itemId !=null ) {
				if(customerId !=null) {
					//TODO: search by itemId && customerId
				}else {
					//TODO: search by itemId
				}
			}else {
				//TODO: search by customerId
			}
		}
		List<Long> ids = saleItemRepo.findIds(numberName, customerId, itemId);
		Page<SaleItem> saleItems = saleItemRepo.findPage(pageable, ids);
//		if(searchType==null || searchType.isBlank() || searchKey==null || searchKey.isBlank()) {
//			sales = saleRepo.findPage(pageable);
//		}else if(searchType.equals("sale") && !searchKey.isBlank()) {
//			sales = saleRepo.findPageBySale(pageable, searchKey);
//		}else if(searchType.equals("item") && !searchKey.isBlank()){
//			sales = saleRepo.findPageByItem(pageable, searchKey);
//		}
//		if(sales == null) {
//			 return Page.empty();
//		}
		Page<SaleItemDto> all = saleItems.map(saleItem -> {
			SaleItemDto dto = new SaleItemDto();
			dto.setId(saleItem.getId());
//			dto.setNumber(sale.getNumber());
//			dto.setName(sale.getName());
//			dto.setDc(sale.getShippingAddress()==null?"":sale.getShippingAddress().getDc());
//			dto.setDate(sale.getDate());
//			dto.setCustomerName(sale.getCustomer()==null?"":sale.getCustomer().getName());
//			dto.setUnitsSold(sale.getUnitsSold());
//			dto.setUnitsScheduled(sale.getUnitsScheduled());
//			dto.setUnitsProduced(sale.getUnitsProduced());
		    return dto;
		});
		return all;
	}

	@GetMapping("/saleItem/{id}")
	ResponseEntity<SaleItem> getSaleItem(@PathVariable Long id) {
		Optional<SaleItem> result = saleItemRepo.getSaleItemById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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