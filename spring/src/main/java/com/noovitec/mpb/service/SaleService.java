package com.noovitec.mpb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.repo.SaleRepo;

public interface SaleService {
	public void updateUnits(List<Long> saleIds);
	public Sale save(Sale sale);

	@Transactional
	@Service
	public class SaleServiceImp implements SaleService {

		private final Logger log = LoggerFactory.getLogger(SaleServiceImp.class);
		private SaleRepo saleRepo;
		@Autowired
		SaleItemRepo saleItemRepo;

		public SaleServiceImp(SaleRepo saleRepo) {
			this.saleRepo = saleRepo;
		}
		
		public Sale save(Sale sale) {
			return saleRepo.save(sale);
		}

		public void updateUnitsByItem(List<Long> itemIds) {
			if(itemIds != null && itemIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Sale> sales = itemIds==null?saleRepo.findAll():saleRepo.findByIds(itemIds);
			for (Sale sale : sales) {
				sale.updateUnits();
				saleRepo.save(sale);
				counter++;
				log.info("Updated Sale: " + sale.getId());
			}
			;
			log.info("Total sales: " + counter);
		}

		public void updateUnits(List<Long> saleIds) {
			if(saleIds != null && saleIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Sale> sales = saleIds==null?saleRepo.findAll():saleRepo.findByIds(saleIds);
			for (Sale sale : sales) {
				sale.updateUnits();
				saleRepo.save(sale);
				counter++;
				log.info("Updated Sale: " + sale.getId());
			}
			;
			log.info("Total sales: " + counter);
		}
	}
}
