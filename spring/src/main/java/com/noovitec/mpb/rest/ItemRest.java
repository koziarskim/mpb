package com.noovitec.mpb.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.repo.ItemRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ItemRest {

	private final Logger log = LoggerFactory.getLogger(ItemRest.class);
	private ItemRepo itemRepo;

	public ItemRest(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@GetMapping("/item")
	Collection<Item> getAll() {
		return itemRepo.findAll();
	}

	@GetMapping("/item/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Item> item = itemRepo.findById(id);
		return item.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/*
	 * Use this to create and update entity. No need to use PUT for update. If ID is
	 * not null, it will try to update.
	 */
	@PostMapping("/item")
	ResponseEntity<Item> post(@Valid @RequestBody Item jsonItem) throws URISyntaxException {
		for (ItemComponent ic : jsonItem.getItemComponents()) {
			if (ic.getId() != null) {
				ic.setItem(jsonItem);
			}
		}
		Item result = itemRepo.save(jsonItem);
		return ResponseEntity.created(new URI("/api/item/" + result.getId())).body(result);
	}

	@DeleteMapping("/item/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		itemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}