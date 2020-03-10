package com.noovitec.mpb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.repo.SaleRepo;

public interface SaleService {
	public void updateUnits(List<Long> salIds);
	public void updateNumber();
	public void mergeSales();

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

		public void updateUnitsByItem(List<Long> itemIds) {
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

		public void updateUnits(List<Long> salIds) {
			Long counter = 0L;
			Iterable<Sale> sales = salIds==null?saleRepo.findAll():saleRepo.findByIds(salIds);
			for (Sale sale : sales) {
				sale.updateUnits();
				saleRepo.save(sale);
				counter++;
				log.info("Updated Sale: " + sale.getId());
			}
			;
			log.info("Total sales: " + counter);
		}

		public void mergeSales() {
			List<SaleItem> saleItems = saleItemRepo.findByHyphen();
			for(SaleItem saleItem: saleItems) {
				int index = saleItem.getSale().getNumber().indexOf("---");
				String number = saleItem.getSale().getNumber().substring(0,index);
				Sale sale = saleItemRepo.getByNumber(number);
				saleItem.setSale(sale);
				if(sale.getSaleItems()==null) {
					sale.setSaleItems(new ArrayList<SaleItem>());
				}
				sale.getSaleItems().add(saleItem);
				saleRepo.save(sale);
				log.info("Sale: "+sale.getId()+" Number: "+sale.getNumber());
			}
			log.info("Done..");
		}
		
		public void updateNumber() {
			Map<String, List<Sale>> names = new HashMap<String, List<Sale>>();
			Iterable<Sale> sales = saleRepo.findAll();
			for (Sale sale : sales) {
				sale.setNumber(sale.getNumber().trim());
				saleRepo.save(sale);
			}
			for (Sale sale : sales) {
				List<Sale> name = names.get(sale.getNumber());
				if(name == null) {
					name = new ArrayList<Sale>();
					names.put(sale.getNumber(), name);
				}
				name.add(sale);
			}
			names.entrySet().forEach(name->{
				if(name.getValue().size()>0) {
					int c = 0;
					int counter = 0;
					for(Sale sale: name.getValue()) {
						if(counter>0) {
							sale.setNumber(name.getKey()+"---"+Integer.toString(counter));
							saleRepo.save(sale);
						}
						counter++;
						c++;
					}
					log.info("Updated: "+c);
				}
			});
		}

	}
}
