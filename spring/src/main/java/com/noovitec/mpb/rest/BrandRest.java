package com.noovitec.mpb.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Brand;
import com.noovitec.mpb.repo.BrandRepo;


@RestController
@RequestMapping("/api")
class BrandRest {

	private final Logger log = LoggerFactory.getLogger(BrandRest.class);
	private BrandRepo brandRepo;

	public BrandRest(BrandRepo brandRepo) {
		this.brandRepo = brandRepo;
	}

	@GetMapping("/brand")
	Collection<Brand> getAll() {
		return brandRepo.findAll();
	}
}