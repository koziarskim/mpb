package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.ReceivingRepo;

public interface ReceivingService {

	public Receiving save(Receiving receiving);
	public void delete(Long id);

	@Transactional
	@Service("receivingServiceImpl")
	public class ReceivingServiceImp implements ReceivingService{

		private final Logger log = LoggerFactory.getLogger(ReceivingServiceImp.class);
		private ReceivingRepo receivingRepo;
		@Autowired
		PurchaseRepo purchaseRepo;
		@Autowired
		ComponentRepo componentRepo;
		@Autowired
		CrudService crudService;


		public ReceivingServiceImp(ReceivingRepo receivingRepo) {
			this.receivingRepo = receivingRepo;
		}

		public Receiving save(Receiving receiving) {
			receiving = (Receiving) crudService.merge(receiving);
			return receivingRepo.save(receiving);
		}
		
		public void delete(Long id) {
			Receiving receiving = receivingRepo.findById(id).get();
			receivingRepo.delete(receiving);
		}
	}
}