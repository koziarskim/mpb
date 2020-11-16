package com.noovitec.mpb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.repo.ItemRepo;

public interface ItemService {

	public Item save(Item item) throws IOException;
	public void delete(Long id);
	public void updateUnits(List<Long> itemIds);
	public void updateUnitsReadyProd(List<Long> itemIds);
	public void updateUnitsByComponent(Long componentId);
//	public void updateUnitsReadyProdByComponent(Long componentId);
	public List<Long> findIdsByComponents(List<Long> componentIds);
	public void asyncUpdateUnits();
	
	@Transactional
	@Service("itemServiceImpl")
	public class ItemServiceImp implements ItemService {

		private final Logger log = LoggerFactory.getLogger(ItemServiceImp.class);
		private ItemRepo itemRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		AttachmentService attachmentService;

		public ItemServiceImp(ItemRepo itemRepo) {
			this.itemRepo = itemRepo;
		}
		
		public Item save(Item item) throws IOException {
			for (ItemComponent ic : item.getItemComponents()) {
				ic.setItem(item);
			}
			return itemRepo.save(item);
		}
		
		public void delete(Long id) {
			itemRepo.deleteById(id);
		}

		public void updateUnitsReadyProd(List<Long> itemIds) {
			if(itemIds != null && itemIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Item> items = itemIds==null?itemRepo.findAll():itemRepo.findByIds(itemIds);
			for (Item item : items) {
//				item.updateUnitsReadyProd();
				itemRepo.save(item);
				counter++;
				log.info("Updated Item: " + item.getId());
			}
			;
			log.info("Total items: " + counter);
		}

		public void updateUnits(List<Long> itemIds) {
			if(itemIds != null && itemIds.size()==0) {
				return;
			}
			Long counter = 0L;
			Iterable<Item> items = itemIds==null?itemRepo.findAll():itemRepo.findByIds(itemIds);
			for (Item item : items) {
				item.updateUnits();
				itemRepo.save(item);
				counter++;
				log.info("Updated Item: " + item.getId());
			}
			;
			log.info("Total items: " + counter);
		}

		public void updateUnitsByComponent(Long componentId) {
			for(Item item: itemRepo.findByComponent(componentId)) {
				item.updateUnits();
				crudService.save(item);
			}
		}

//		public void updateUnitsReadyProdByComponent(Long componentId) {
//			for(Item item: itemRepo.findByComponent(componentId)) {
//				item.updateUnitsReadyProd();
//				crudService.save(item);
//			}
//		}
		
		public List<Long> findIdsByComponents(List<Long> componentIds){
			if(componentIds != null && componentIds.size()==0) {
				return new ArrayList<Long>();
			}
			List<Long> ids = itemRepo.findIdsByComponents(componentIds);
			return ids;
		}
		
		@Async
		public void asyncUpdateUnits() {
			log.info("Running async.....");
			Long count = 1000000000L;
			while(count > 0L) {
				count--;
			}
			log.info("Service done");
		}
	}
}
