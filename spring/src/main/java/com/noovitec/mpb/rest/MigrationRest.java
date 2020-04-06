package com.noovitec.mpb.rest;

import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.exceptions.RepoException;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.MigrationRepo;
import com.noovitec.mpb.service.AttachmentService;
import com.noovitec.mpb.service.MigrationService;

@Transactional
@RestController
@RequestMapping("/api")
class MigrationRest {

	private final Logger log = LoggerFactory.getLogger(MigrationRest.class);
	private MigrationService migrationService;
	@Autowired
	ItemRepo itemRepo;
	@Autowired
	ComponentRepo componentRepo;
	@Autowired
	AttachmentRepo attachmentRepo;
	@Autowired
	AttachmentService attachmentService;
	@Autowired
	MigrationRepo migrationRepo;

	public MigrationRest(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@GetMapping("/migrate/db")
	ResponseEntity<?> migrate() throws IOException {
		return ResponseEntity.ok().body("OK");
	}
	
	@GetMapping("/migrate/tenant/from/{tenantFrom}/to/{tenantTo}")
	ResponseEntity<?> migrate(@PathVariable String tenantFrom, @PathVariable String tenantTo) throws IOException, RepoException {
		migrationService.createTenant(tenantFrom, tenantTo);
		return ResponseEntity.ok().body("OK");
	}
	
}