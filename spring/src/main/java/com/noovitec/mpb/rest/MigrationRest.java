package com.noovitec.mpb.rest;

import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.exceptions.RepoException;
import com.noovitec.mpb.repo.MigrationRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.MigrationService;

@Transactional
@RestController
@RequestMapping("/api")
class MigrationRest {

	private final Logger log = LoggerFactory.getLogger(MigrationRest.class);
	private MigrationService migrationService;
	@Autowired
	ItemService itemService;
	@Autowired
	MigrationRepo migrationRepo;
	@Autowired
	CrudService crudService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TaskExecutor taskExecutor;

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
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				MpbTenantContext.setCurrentTenant(tenantTo);
				itemService.updateUnits(null);
				MpbTenantContext.clear();
			}
		});
		return ResponseEntity.ok().body("OK");
	}
	
	ResponseEntity<?> _migrate(@PathVariable String tenantFrom, @PathVariable String tenantTo) throws IOException, RepoException {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NEVER);
		transactionTemplate.execute(status-> {
			try {
				migrationService.createTenant(tenantFrom, tenantTo);
			} catch (IOException | RepoException e) {
				e.printStackTrace();
			}
			crudService.flush();
			crudService.clear();
			return "Ref-1";
		});

		String prevTenant = MpbTenantContext.getCurrentTenant();
		MpbTenantContext.setCurrentTenant(tenantTo);
		itemService.updateUnits(null);
		MpbTenantContext.setCurrentTenant(prevTenant);
		return ResponseEntity.ok().body("OK");
	}
	
}