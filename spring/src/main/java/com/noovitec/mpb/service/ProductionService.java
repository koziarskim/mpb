package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.repo.ProductionRepo;

public interface ProductionService {

	public Production save(Production production);
	public void delete(Long id);

	@Transactional
	@Service("productionServiceImpl")
	public class ProductionServiceImp implements ProductionService {

		private final Logger log = LoggerFactory.getLogger(ProductionServiceImp.class);
		private ProductionRepo productionRepo;
		@Autowired
		CrudService crudService;

		public ProductionServiceImp(ProductionRepo productionRepo) {
			this.productionRepo = productionRepo;
		}
		
		public Production save(Production production) {
			production = (Production) crudService.merge(production);
			return productionRepo.save(production);
		}
		
		public void delete(Long id) {
			productionRepo.deleteById(id);
		}
	}
}
