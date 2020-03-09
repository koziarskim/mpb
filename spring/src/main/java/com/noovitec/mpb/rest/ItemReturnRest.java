package com.noovitec.mpb.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.ItemReturn;
import com.noovitec.mpb.entity.SaleItemReturn;
import com.noovitec.mpb.repo.ItemReturnRepo;
import com.noovitec.mpb.service.ItemService;

@RestController
@RequestMapping("/api")
class ItemReturnRest {

	private ItemReturnRepo itemReturnRepo;
	
	private final Logger log = LoggerFactory.getLogger(ItemReturnRest.class);
	@Autowired
	ItemService itemService;
	
	public ItemReturnRest(ItemReturnRepo itemReturnRepo) {
		this.itemReturnRepo = itemReturnRepo;
	}

	@GetMapping("/itemReturn")
	List<ItemReturn> getAll(
			@RequestParam(required = false) Long itemId, 
			@RequestParam(required = false) Long saleId ) {
		if(itemId !=null) {
			return itemReturnRepo.findByItem(itemId);
		};
		if(saleId !=null) {
			return itemReturnRepo.findBySale(saleId);
		};
		if(itemId !=null && saleId !=null) {
			return itemReturnRepo.findByItemAndSale(itemId, saleId);
		};
		return itemReturnRepo.findAll();
	}

	@GetMapping("/itemReturn/{id}")
	ResponseEntity<ItemReturn> get(@PathVariable Long id) {
		Optional<ItemReturn> itemReturn = itemReturnRepo.findById(id);
		return ResponseEntity.ok(itemReturn.get());
	}

	@PostMapping("/itemReturn")
	ResponseEntity<ItemReturn> post(@RequestBody ItemReturn itemReturn){
		for(SaleItemReturn sir: itemReturn.getSaleItemReturns()) {
			sir.setItemReturn(itemReturn);
		}
		ItemReturn result = itemReturnRepo.save(itemReturn);
		itemService.updateUnits(Arrays.asList(itemReturn.getItem().getId()));
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/itemReturn/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ItemReturn itemReturn = itemReturnRepo.findById(id).get();
		Long itemId = itemReturn.getItem().getId();
		itemReturnRepo.deleteById(id);
		itemService.updateUnits(Arrays.asList(itemId));
		return ResponseEntity.ok().build();
	}
}