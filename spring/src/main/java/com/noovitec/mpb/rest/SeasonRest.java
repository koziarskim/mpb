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

import com.noovitec.mpb.entity.Season;
import com.noovitec.mpb.repo.SeasonRepo;


@RestController
@RequestMapping("/api")
class SeasonRest {

	private final Logger log = LoggerFactory.getLogger(SeasonRest.class);
	private SeasonRepo seasonRepo;

	public SeasonRest(SeasonRepo seasonRepo) {
		this.seasonRepo = seasonRepo;
	}
	
	@GetMapping("/season")
	Collection<Season> getAll() {
		return seasonRepo.findAll();
	}
	
	@GetMapping("/season/{id}")
	ResponseEntity<Season> getSeason(@PathVariable Long id) {
		Optional<Season> season = seasonRepo.findById(id);
		return ResponseEntity.ok().body(season.get());
	}
}