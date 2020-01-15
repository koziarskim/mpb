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

import com.noovitec.mpb.entity.Year;
import com.noovitec.mpb.repo.YearRepo;

@RestController
@RequestMapping("/api")
class YearRest {

	private final Logger log = LoggerFactory.getLogger(YearRest.class);
	private YearRepo yearRepo;

	public YearRest(YearRepo yearRepo) {
		this.yearRepo = yearRepo;
	}

	@GetMapping("/year")
	Collection<Year> getAll() {
		return yearRepo.findAll();
	}

	@GetMapping("/year/{id}")
	ResponseEntity<Year> getYear(@PathVariable Long id) {
		Optional<Year> year = yearRepo.findById(id);
		return ResponseEntity.ok().body(year.get());
	}
}