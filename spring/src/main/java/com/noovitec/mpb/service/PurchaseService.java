package com.noovitec.mpb.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.SupplierRepo;

public interface PurchaseService {

	public Purchase save(Purchase purchase) throws IOException;

	@Transactional
	@Service("purchaseServiceImpl")
	public class PurchaseServiceImp implements PurchaseService {

		private final Logger log = LoggerFactory.getLogger(PurchaseServiceImp.class);
		private PurchaseRepo purchaseRepo;
		@Autowired
		private SupplierRepo supplierRepo;
		@Autowired
		private ComponentRepo componentRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		AttachmentService attachmentService;
		
		public PurchaseServiceImp(PurchaseRepo purchaseRepo) {
			this.purchaseRepo = purchaseRepo;
		}
		
		public Purchase save(Purchase purchase) throws IOException {
			if (purchase == null) {
				purchase = new Purchase();
			}
			purchase = (Purchase) crudService.merge(purchase);
			purchase = purchaseRepo.save(purchase);
			return purchase;
		}
		
	}
}
