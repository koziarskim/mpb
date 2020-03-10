package com.noovitec.mpb.rest;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.SaleItemReturn;
import com.noovitec.mpb.repo.SaleItemReturnRepo;
import com.noovitec.mpb.service.ItemService;

@RestController
@RequestMapping("/api")
class SaleItemReturnRest {

	private SaleItemReturnRepo saleItemReturnRepo;
	
	private final Logger log = LoggerFactory.getLogger(SaleItemReturnRest.class);
	@Autowired
	ItemService itemService;
	
	public SaleItemReturnRest(SaleItemReturnRepo saleItemReturnRepo) {
		this.saleItemReturnRepo = saleItemReturnRepo;
	}

	@GetMapping("/saleItemReturn")
	List<SaleItemReturn> getAll(
			@RequestParam(required = false) Long itemId, 
			@RequestParam(required = false) Long saleId ) {
		if(itemId !=null) {
			return saleItemReturnRepo.findByItem(itemId);
		};
		if(saleId !=null) {
			return saleItemReturnRepo.findBySale(saleId);
		};
		if(itemId !=null && saleId !=null) {
			return saleItemReturnRepo.findByItemAndSale(itemId, saleId);
		};
		return saleItemReturnRepo.findAll();
	}

	@DeleteMapping("/saleItemReturn/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		SaleItemReturn saleItemReturn = saleItemReturnRepo.findById(id).get();
		Long itemId = saleItemReturn.getItemReturn().getItem().getId();
		saleItemReturnRepo.deleteById(id);
		itemService.updateUnits(Arrays.asList(itemId));
		return ResponseEntity.ok().build();
	}
}