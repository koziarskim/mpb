package com.noovitec.mpb.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.service.MigrationService;

@RestController
@RequestMapping("/api")
class MigrationRest {

	private final Logger log = LoggerFactory.getLogger(MigrationRest.class);
	private MigrationService migrationService;

	public MigrationRest(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@GetMapping("/migrate/db")
	ResponseEntity<?> migrate() {

		return ResponseEntity.ok().build();
	}
}