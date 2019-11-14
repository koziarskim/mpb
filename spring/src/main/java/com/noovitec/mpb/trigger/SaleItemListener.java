package com.noovitec.mpb.trigger;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.SaleRepo;

public class SaleItemListener {
	
	private final Logger log = LoggerFactory.getLogger(SaleItemListener.class);
	
    @PrePersist
    public void prePersist(SaleItem saleItem) {
    	//Update Item
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
		item.setUnitsProduced(item.getUnitsProduced() + saleItem.getUnitsProduced());
		item.setUnitsSold(item.getUnitsSold() + saleItem.getUnits());
		item.setUnitsScheduled(item.getUnitsScheduled() + saleItem.getUnitsScheduled());
		itemRepo.save(item);
    	//Update Sale
    	SaleRepo saleRepo = BeanUtil.getBean(SaleRepo.class);
    	Sale sale = saleRepo.findById(saleItem.getSale().getId()).get();
    	sale.setUnitsProduced(sale.getUnitsProduced() + saleItem.getUnitsProduced());
		sale.setUnitsSold(sale.getUnitsSold() + saleItem.getUnits());
		sale.setUnitsScheduled(sale.getUnitsScheduled() + saleItem.getUnitsScheduled());
    	saleRepo.save(sale);

    }
    
    @PreUpdate
    public void preUpdate(SaleItem saleItem) {
    	//Update item
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
    	Long unitsProducedForItem = 0L;
    	Long unitsSoldForItem = 0L;
    	Long unitsScheduledForItem = 0L;
    	for(SaleItem si: item.getSaleItems()) {
    		unitsProducedForItem += si.getUnitsProduced();
    		unitsSoldForItem += si.getUnits();
    		unitsScheduledForItem += si.getUnitsScheduled();
    	}
		item.setUnitsProduced(unitsProducedForItem);
		item.setUnitsSold(unitsSoldForItem);
		item.setUnitsScheduled(unitsScheduledForItem);
		itemRepo.save(item);
		//Update sale
    	SaleRepo saleRepo = BeanUtil.getBean(SaleRepo.class);
    	Sale sale = saleRepo.findById(saleItem.getSale().getId()).get();
    	Long unitsProducedForSale = 0L;
    	Long unitsSoldForSale = 0L;
    	Long unitsScheduledForSale = 0L;
    	for(SaleItem si: sale.getSaleItems()) {
    		unitsProducedForSale += si.getUnitsProduced();
    		unitsSoldForSale += si.getUnits();
    		unitsScheduledForSale += si.getUnitsScheduled();
    	}
		sale.setUnitsProduced(unitsProducedForSale);
		sale.setUnitsSold(unitsSoldForSale);
		sale.setUnitsScheduled(unitsScheduledForSale);
		saleRepo.save(sale);
    }
    
    @PreRemove
    public void preRemove(SaleItem saleItem) {
    	//Update Item
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
    	item.setUnitsProduced(item.getUnitsProduced() - saleItem.getUnitsProduced());
		item.setUnitsSold(item.getUnitsSold() - saleItem.getUnits());
		item.setUnitsScheduled(item.getUnitsScheduled() - saleItem.getUnitsScheduled());
    	itemRepo.save(item);
    	//Update Sale
    	SaleRepo saleRepo = BeanUtil.getBean(SaleRepo.class);
    	Sale sale = saleRepo.findById(saleItem.getSale().getId()).get();
    	sale.setUnitsProduced(sale.getUnitsProduced() - saleItem.getUnitsProduced());
		sale.setUnitsSold(sale.getUnitsSold() - saleItem.getUnits());
		sale.setUnitsScheduled(sale.getUnitsScheduled() - saleItem.getUnitsScheduled());
    	saleRepo.save(sale);
    }
}
