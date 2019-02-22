package com.noovitec.mpb.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Category;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.repo.CategoryRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class CategoryRest {

	private final Logger log = LoggerFactory.getLogger(CategoryRest.class);
	private CategoryRepo categoryRepo;

	public CategoryRest(CategoryRepo categoryRepo) {
		this.categoryRepo = categoryRepo;
	}

	@GetMapping("/category")
	Collection<Category> getAll() {
		return categoryRepo.findAll();
	}
	
	@GetMapping("/category/type/{type}")
	Collection<Category> getAllForComponent(@PathVariable String type) {
		return categoryRepo.findAllByType(type);
	}
}