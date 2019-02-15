package com.noovitec.mpb.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Upc;
import com.noovitec.mpb.repo.UpcRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class UpcRest {

	private final Logger log = LoggerFactory.getLogger(UpcRest.class);
	private UpcRepo upcRepo;

	public UpcRest(UpcRepo upcRepo) {
		this.upcRepo = upcRepo;
	}

	@GetMapping("/upc")
	Collection<Upc> getAll() {
		return upcRepo.findAll();
	}

	@GetMapping("/upc/available")
	Upc getAvailableCode() {
		Upc upc = upcRepo.getFirstAvailable();
		return upc;
	}
}