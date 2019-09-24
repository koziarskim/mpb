package com.noovitec.mpb.rest;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Category;
import com.noovitec.mpb.repo.CategoryRepo;

@RestController
@RequestMapping("/api")
class CategoryRest {

	private final Logger log = LoggerFactory.getLogger(CategoryRest.class);
	private CategoryRepo categoryRepo;

	public CategoryRest(CategoryRepo categoryRepo) {
		this.categoryRepo = categoryRepo;
	}

	@GetMapping("/category/{id}")
	ResponseEntity<Category> getCategory(@PathVariable Long id) {
		Optional<Category> category = categoryRepo.findById(id);
		return ResponseEntity.ok().body(category.get());
	}

	@GetMapping("/category")
	Collection<Category> getAll() {
		return categoryRepo.findAll();
	}

	@GetMapping("/category/item/keyValue")
	Collection<KeyValueDto> getAllForItemKeyValue() {
		return categoryRepo.getAllForItemKeyValue();
	}

	@GetMapping("/category/component/kv")
	Collection<KeyValueDto> getAllForComponentKeyValue() {
		return categoryRepo.getAllForComponentKeyValue();
	}

	@GetMapping("/category/type/{type}")
	Collection<Category> getAllForComponent(@PathVariable String type) {
		return categoryRepo.findAllByType(type);
	}

}