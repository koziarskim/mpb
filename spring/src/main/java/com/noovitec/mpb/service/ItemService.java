package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.ItemRepo;

public interface ItemService {

	public void updateUnits();

	@Transactional
	@Service("itemServiceImpl")
	public class ItemServiceImp implements ItemService {

		private final Logger log = LoggerFactory.getLogger(ItemServiceImp.class);
		private ItemRepo itemRepo;

		public ItemServiceImp(ItemRepo itemRepo) {
			this.itemRepo = itemRepo;
		}

		public void updateUnits() {
			Long counter = 0L;
			Iterable<Item> items = itemRepo.findAll();
			for (Item item : items) {
				item.updateUnits();
				itemRepo.save(item);
				counter++;
				log.info("Updated Item: " + item.getId());
			}
			;
			log.info("Total items: " + counter);
		}

	}
}
