package com.noovitec.mpb.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.MigrationRepo;

public interface MigrationService {
	
	public void createTenant(String tenantFrom, String tenantTo) throws IOException;

	@Transactional
	@Service("migrationServiceImpl")
	public class MigrationServiceImpl implements MigrationService {
		
		MigrationRepo migrationRepo;
		
		public MigrationServiceImpl(MigrationRepo migrationRepo) {
			this.migrationRepo = migrationRepo;
		}
		
		public void createTenant(String tenantFrom, String tenantTo) throws IOException {
			migrationRepo.createTenant(tenantFrom, tenantTo);
		}
	}
}
