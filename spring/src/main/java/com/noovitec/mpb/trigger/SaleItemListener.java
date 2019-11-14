package com.noovitec.mpb.trigger;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.ItemRepo;

public class SaleItemListener {
	
	private final Logger log = LoggerFactory.getLogger(SaleItemListener.class);
	
    @PrePersist
    public void prePersist(SaleItem saleItem) {
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
		item.setUnitsProduced(item.getUnitsProduced() + saleItem.getUnitsProduced());
		item.setUnitsSold(item.getUnitsSold() + saleItem.getUnits());
		itemRepo.save(item);
    }
    
    @PreUpdate
    public void preUpdate(SaleItem saleItem) {
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
    	Long unitsProduced = 0L;
    	Long unitsSold = 0L;
    	for(SaleItem si: item.getSaleItems()) {
    		unitsProduced += si.getUnitsProduced();
    		unitsSold += si.getUnits();
    	}
		item.setUnitsProduced(unitsProduced);
		item.setUnitsSold(unitsSold);
		itemRepo.save(item);
    }
    
    @PreRemove
    public void preRemove(SaleItem saleItem) { 
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
    	Item item = itemRepo.findById(saleItem.getItem().getId()).get();
    	item.setUnitsProduced(item.getUnitsProduced() - saleItem.getUnitsProduced());
		item.setUnitsSold(item.getUnitsSold() - saleItem.getUnits());
    	itemRepo.save(item);
    }
}
