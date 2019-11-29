package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

public interface MigrationService {

	@Transactional
	@Service
	public class MigrationServiceImpl implements MigrationService {

	}
}
