package com.noovitec.mpb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.repo.SaleRepo;

public interface SaleService {
	public void updateUnits(List<Long> salIds);

	@Transactional
	@Service
	public class SaleServiceImp implements SaleService {

		private final Logger log = LoggerFactory.getLogger(SaleServiceImp.class);
		private SaleRepo saleRepo;

		public SaleServiceImp(SaleRepo saleRepo) {
			this.saleRepo = saleRepo;
		}

		public void updateUnits(List<Long> salIds) {
			Long counter = 0L;
			Iterable<Sale> items = salIds==null?saleRepo.findAll():saleRepo.findByIds(salIds);
			Iterable<Sale> sales = items;
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
