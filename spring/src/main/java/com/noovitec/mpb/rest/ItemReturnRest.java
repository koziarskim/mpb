package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.ItemReturn;
import com.noovitec.mpb.entity.SaleItemReturn;
import com.noovitec.mpb.repo.ItemReturnRepo;

@RestController
@RequestMapping("/api")
class ItemReturnRest {

	private ItemReturnRepo itemReturnRepo;
	
	private final Logger log = LoggerFactory.getLogger(ItemReturnRest.class);
	
	public ItemReturnRest(ItemReturnRepo itemReturnRepo) {
		this.itemReturnRepo = itemReturnRepo;
	}

	@GetMapping("/itemReturn")
	ResponseEntity<List<ItemReturn>> getAll() {
		List<ItemReturn> itemReturns = itemReturnRepo.findAll();
		return ResponseEntity.ok(itemReturns);
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
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/itemReturn/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		itemReturnRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}