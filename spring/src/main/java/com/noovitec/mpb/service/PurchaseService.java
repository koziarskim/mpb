package com.noovitec.mpb.service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.repo.PurchaseRepo;

public interface PurchaseService {

	public Purchase save(Purchase purchase) throws IOException;
	public void updateUnits(List<Long> purchaseIds);

	@Transactional
	@Service("purchaseServiceImpl")
	public class PurchaseServiceImp implements PurchaseService {

		private final Logger log = LoggerFactory.getLogger(PurchaseServiceImp.class);
		private PurchaseRepo purchaseRepo;
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

		public void updateUnits(List<Long> purchaseIds) {
			if(purchaseIds != null && purchaseIds.size()==0) {
				return;
			}
			Iterable<Purchase> purchases = purchaseIds==null?purchaseRepo.findAll():purchaseRepo.findByIds(purchaseIds);
			for(Purchase purchase: purchases) {
				for(PurchaseComponent pc: purchase.getPurchaseComponents()) {
					pc.updateUnits();
				}
				purchase.updateUnits();
				purchaseRepo.save(purchase);
			}
		}
		
	}
}
