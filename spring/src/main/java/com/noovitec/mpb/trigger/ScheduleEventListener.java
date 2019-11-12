package com.noovitec.mpb.trigger;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.ProductionRepo;

public class ScheduleEventListener {
	
	private final Logger log = LoggerFactory.getLogger(ScheduleEventListener.class);
	
    @PostPersist
    public void prePersist(Receiving target) {
    	this.perform(target);
    }
    @PostUpdate
    public void preUpdate(Receiving target) { 
    	this.perform(target);
    }
    @PostRemove
    public void preRemove(Receiving target) { 
    	this.perform(target);
    }
    

    @Transactional()
    private void perform(Receiving target) {
    	ProductionRepo productionRepo = BeanUtil.getBean(ProductionRepo.class);
        Production production = productionRepo.findById(95L).get();
//        log.info(item.getName());
//        entityManager.persist(new FileHistory(target, action));
    }
}
