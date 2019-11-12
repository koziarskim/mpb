package com.noovitec.mpb.trigger;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ScheduleEventRepo;

public class ProductionListener {
	
	private final Logger log = LoggerFactory.getLogger(ProductionListener.class);
	
    @PrePersist
    public void prePersist(Production production) {
    	ScheduleEventRepo scheduleEventRepo = BeanUtil.getBean(ScheduleEventRepo.class);
    	ScheduleEvent se = scheduleEventRepo.getOne(production.getScheduleEvent().getId());
		Long units = 0L;
		for (Production p : se.getProductions()) {
			units += p.getUnitsProduced();
		}
		se.setUnitsProduced(units);
		scheduleEventRepo.save(se);
    }
    
    @PreUpdate
    public void preUpdate(Production production) { 
    	ScheduleEventRepo scheduleEventRepo = BeanUtil.getBean(ScheduleEventRepo.class);
    	ScheduleEvent se = scheduleEventRepo.getOne(production.getScheduleEvent().getId());
		Long units = 0L;
		for (Production p : se.getProductions()) {
			units += p.getUnitsProduced();
		}
		se.setUnitsProduced(units);
    	scheduleEventRepo.save(se);
    }
    
    @PreRemove
    public void preRemove(Production production) { 
    	ScheduleEventRepo scheduleEventRepo = BeanUtil.getBean(ScheduleEventRepo.class);
		production.getScheduleEvent().setUnitsProduced(production.getScheduleEvent().getUnitsProduced() - production.getUnitsProduced());
    	scheduleEventRepo.save(production.getScheduleEvent());
    }
}
