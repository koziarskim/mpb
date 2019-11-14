package com.noovitec.mpb.trigger;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.SaleItemRepo;

public class ShipmentItemListener {
	
	private final Logger log = LoggerFactory.getLogger(ShipmentItemListener.class);
	
    @PrePersist
    public void prePersist(ShipmentItem shipmentItem) {
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(shipmentItem.getSaleItem().getId()).get();
		si.setUnitsShipped(si.getUnitsShipped() + shipmentItem.getUnits());
		saleItemRepo.save(si);
    }
    
    @PreUpdate
    public void preUpdate(ShipmentItem shipmentItem) {
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(shipmentItem.getSaleItem().getId()).get();
    	Long unitsShipped = 0L;
    	for(ShipmentItem shipItem: si.getShipmentItems()) {
    		unitsShipped += shipItem.getUnits();
    	}
		si.setUnitsShipped(unitsShipped);
		saleItemRepo.save(si);
    }
    
    @PreRemove
    public void preRemove(ShipmentItem shipmentItem) { 
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(shipmentItem.getSaleItem().getId()).get();
		si.setUnitsShipped(si.getUnitsShipped() - shipmentItem.getUnits());
		saleItemRepo.save(si);
    }
}
