package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Address;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.AddressRepo;
import com.noovitec.mpb.repo.ItemRepo;

/*
 * This is the API to update local db but running script. 
 * Make sure you know what you are doing!!!!
 */

@RestController
@RequestMapping("/api")
class MigrationRest {

	private final Logger log = LoggerFactory.getLogger(MigrationRest.class);
	
	@Autowired
	private ItemRepo itemRepo;

	@GetMapping("/migrate/db")
	ResponseEntity<?> migrate() {
		Iterable<Item> items = itemRepo.findAll();
		for(Item item: items) {
			Long unitsProduced = item.getUnitsProduced();
			itemRepo.updateUnitsOnStock(unitsProduced, item.getId());
		}
		return ResponseEntity.ok().build();
	}
}