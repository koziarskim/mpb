package com.noovitec.mpb.rest;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class SaleItemRest {

	private final Logger log = LoggerFactory.getLogger(SaleItemRest.class);
	private SaleItemRepo saleItemRepo;

	@Autowired
	CrudService crudService;
	@Autowired
	SaleService saleService;

	public SaleItemRest(SaleItemRepo saleItemRepo) {
		this.saleItemRepo = saleItemRepo;
	}

	@GetMapping("/saleItem")
	List<SaleItem> getAll(@RequestParam(name = "ids", required = false) Long[] ids) {
		return saleItemRepo.findAllByIds(ids);
	}

	@GetMapping("/saleItem/pageable")
	Page<SaleItemDto> getAllPageable(
			@RequestParam Pageable pageable, 
			@RequestParam(required = false) String numberName,
			@RequestParam(required = false) Long saleId,
			@RequestParam(required = false) Long itemId, 
			@RequestParam(required = false) Long customerId, 
			@RequestParam(required = false) String status) {
		Page<SaleItem> saleItems = saleItemRepo.findPageable(pageable, numberName, saleId, customerId, itemId, status);
		return this.mapToDto(saleItems);
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
	Collection<KeyValueDto> getAllBySale(@PathVariable Long sale_id) {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findSaleItemsBySale(sale_id);
		return saleDtos;
	}

	@GetMapping("/saleItem/kv/transfer/item/{item_id}")
	List<KeyValueDto> getAllTrasferByItem(@PathVariable Long item_id) {
		List<SaleItem> saleItems = saleItemRepo.findKvTrasferByItem(item_id);
		List<KeyValueDto> dtos = new ArrayList<KeyValueDto>();
		for(SaleItem saleItem: saleItems) {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(saleItem.getId());
			dto.setName(saleItem.getSale().getNumber() + "("+saleItem.getSale().getCustomer().getName()+"), S: "+saleItem.getUnitsOnStock()+", R: "+saleItem.getUnitsReturned());
			dtos.add(dto);
		}		

		return dtos;
	}
	
	@GetMapping("/saleItem/kv/item/{item_id}")
	Collection<KeyValueDto> getAllByItem(@PathVariable Long item_id) {
		Collection<KeyValueDto> dtos = saleItemRepo.findKvByItem(item_id);
		return dtos;
	}

	@GetMapping("/saleItem/kv/item/{itemId}/customer/{customerId}")
	Collection<KeyValueDto> getAllByItemAndCustomer(@PathVariable Long itemId, @PathVariable Long customerId) {
		Collection<KeyValueDto> dtos = saleItemRepo.findKvByItemAndCustomer(itemId, customerId);
		return dtos;
	}

	@GetMapping("/saleItem/kv")
	Collection<KeyValueDto> getAllKvs() {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findAllKvs();
		return saleDtos;
	}

	@PostMapping("/saleItem/{saleItemId}/move/to/sale/{saleId}")
	ResponseEntity<?> moveSaleItem(@PathVariable Long saleItemId, @PathVariable Long saleId) {
		SaleItem si = saleItemRepo.getSaleItemById(saleItemId).get();
		Long oldSaleId = si.getSale().getId();
		saleItemRepo.moveSaleItem(saleItemId, saleId);
		saleService.updateUnits(Arrays.asList(oldSaleId, saleId));
		return ResponseEntity.ok().build();
	}
	
	private Page<SaleItemDto> mapToDto(Page<SaleItem> saleItems) {
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
			dto.setDc(saleItem.getSale().getShippingAddress() != null
					? saleItem.getSale().getShippingAddress().getDc() + " (" + saleItem.getSale().getShippingAddress().getState()
					: "");
			dto.setUnitsSold(Long.valueOf(saleItem.getUnits()));
			dto.setUnitsScheduled(saleItem.getUnitsScheduled());
			dto.setUnitsProduced(saleItem.getUnitsProduced());
			dto.setUnitsShipped(saleItem.getUnitsShipped());
			dto.setUnitsOnStock(saleItem.getUnitsOnStock());
			dto.setUnitsTransferedTo(saleItem.getUnitsTransferedTo());
			dto.setUnitsTranferedFrom(saleItem.getUnitsTransferedFrom());
			dto.setUnitsAdjusted(saleItem.getUnitsAdjusted());
			dto.setStatus(saleItem.getStatus());
			return dto;
		});
		return all;
	}

}