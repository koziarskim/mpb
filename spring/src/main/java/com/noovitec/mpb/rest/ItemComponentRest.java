package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;

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

import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.repo.ItemComponentRepo;


@RestController
@RequestMapping("/api")
class ItemComponentRest {

	private final Logger log = LoggerFactory.getLogger(ItemComponentRest.class);
	ItemComponentRepo itemComponentRepo;

	public ItemComponentRest(ItemComponentRepo itemComponentRepo) {
		this.itemComponentRepo = itemComponentRepo;
	}

	@GetMapping("/itemComponent")
	Collection<ItemComponent> getAll() {
		return itemComponentRepo.findAll();
	}

	@GetMapping("/itemComponent/item/{item_id}")
	Collection<ItemComponent> getByItem(@PathVariable Long item_id) {
		Collection<ItemComponent> result = itemComponentRepo.findByItem(item_id);
		return result;
	}

	@PostMapping("/itemComponent")
	ResponseEntity<?> post(@RequestBody(required = false) ItemComponent itemComponent) throws URISyntaxException {
		ItemComponent result = itemComponentRepo.save(itemComponent);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/itemComponent/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ItemComponent itemComponent = itemComponentRepo.findById(id).get();
		itemComponentRepo.delete(itemComponent);
		return ResponseEntity.ok().build();
	}
}