package com.noovitec.mpb.trigger;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.SaleItemRepo;

public class ScheduleEventListener {
	
	private final Logger log = LoggerFactory.getLogger(ScheduleEventListener.class);
	
    @PrePersist
    public void prePersist(ScheduleEvent scheduleEvent) {
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(scheduleEvent.getSaleItem().getId()).get();
    	si.setUnitsProduced(si.getUnitsProduced() + scheduleEvent.getUnitsProduced());
    	si.setUnitsScheduled(si.getUnitsScheduled() + scheduleEvent.getUnitsScheduled());
    	saleItemRepo.save(si);
    }
    
    @PreUpdate
    public void preUpdate(ScheduleEvent scheduleEvent) {
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(scheduleEvent.getSaleItem().getId()).get();
    	Long unitsProduced = 0L;
    	Long unitsScheduled = 0L;
    	for(ScheduleEvent se: si.getScheduleEvents()) {
    		unitsProduced += se.getUnitsProduced();
    		unitsScheduled += se.getUnitsScheduled();
    	}
    	si.setUnitsProduced(unitsProduced);
    	si.setUnitsScheduled(unitsScheduled);
    	saleItemRepo.save(si);
    }
    
    @PreRemove
    public void preRemove(ScheduleEvent scheduleEvent) { 
    	SaleItemRepo saleItemRepo = BeanUtil.getBean(SaleItemRepo.class);
    	SaleItem si = saleItemRepo.findById(scheduleEvent.getSaleItem().getId()).get();
    	si.setUnitsProduced(si.getUnitsProduced() - scheduleEvent.getUnitsProduced());
    	si.setUnitsScheduled(si.getUnitsScheduled() - scheduleEvent.getUnitsScheduled());
    	saleItemRepo.save(si);
    }
}
