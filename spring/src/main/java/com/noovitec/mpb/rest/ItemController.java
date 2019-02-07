package com.noovitec.mpb.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.hibernate.mapping.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ItemController {

	private final Logger log = LoggerFactory.getLogger(ItemController.class);
	private ItemRepo itemRepo;
	@Autowired
	private ComponentRepo componentRepo;
	@PersistenceContext
	private EntityManager em;

	public ItemController(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@GetMapping("/items")
	Collection<Item> getAll() {
//		Collection<Item> items = itemRepo.findAll();
//		Item item = items.iterator().next();
//		Component comp = item.getComponents().iterator().next();
//		Long id = comp.getId();
		return itemRepo.findAll();
	}

	@GetMapping("/items/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Item> item = itemRepo.findById(id);
		return item.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/items")
	ResponseEntity<Item> post(@Valid @RequestBody Item item) throws URISyntaxException {
		Item result = itemRepo.save(item);
		Collection<Component> allComp = componentRepo.findAll();
		Component component = allComp.iterator().next();
//		Component compNew = new Component();
		Long id = component.getId();
		if(component!=null) {
//			component.setItem(result);
//			componentRepo.save(component);
			Collection<Component> components = new HashSet<Component>();
			components.add(component);
			result.setComponents(components);
		}
		itemRepo.save(item);
//		return ResponseEntity.created(new URI("/api/items/" + result.getId())).body(result);
		return null;
	}

	@PutMapping("/items")
	ResponseEntity<Item> put(@Valid @RequestBody Item item) {
		Long id = item.getId();
		Optional<Item> existing = itemRepo.findById(id);
		if (existing.isPresent()) {
			existing.get().setName(item.getName());
			existing.get().setStockNumber(item.getStockNumber());
			existing.get().setDescription(item.getDescription());
			existing.get().setAssumedPrice(item.getAssumedPrice());
		}

		Item result = itemRepo.save(existing.get());
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/items/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		itemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}