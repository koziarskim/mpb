package com.noovitec.mpb.trigger;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noovitec.mpb.app.BeanUtil;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.repo.ItemRepo;

public class ProductionListener {
	
	private final Logger log = LoggerFactory.getLogger(ProductionListener.class);
	
    @PostPersist
    public void prePersist(Production target) {
    	log.info("@PostPersist: "+ target.getId());
    	this.perform(target);
    }
    @PostUpdate
    public void preUpdate(Production target) { 
    	log.info("@PostUpdate: "+ target.getId());
    	this.perform(target);
    }
    @PostRemove
    public void preRemove(Production target) { 
    	log.info("@PostRemove: "+ target.getId());
    	this.perform(target);
    }
    

    @Transactional()
    private void perform(Production target) {
    	ItemRepo itemRepo = BeanUtil.getBean(ItemRepo.class);
        Item item = itemRepo.findById(95L).get();
        log.info(item.getName());
//        entityManager.persist(new FileHistory(target, action));
    }
}
