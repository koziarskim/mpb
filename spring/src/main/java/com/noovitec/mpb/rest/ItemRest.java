package com.noovitec.mpb.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.ItemDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.Season;
import com.noovitec.mpb.entity.Upc;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ReceivingRepo;
import com.noovitec.mpb.repo.SeasonRepo;
import com.noovitec.mpb.repo.UpcRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ItemRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;
	@Autowired
	SeasonRepo seasonRepo;
	@Autowired
	UpcRepo upcRepo;
	@Autowired
	ReceivingRepo receivingRepo;

	private final Logger log = LoggerFactory.getLogger(ItemRest.class);
	private ItemRepo itemRepo;

	public ItemRest(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@GetMapping("/item")
	Collection<Item> getAll() {
		return itemRepo.findAll();
	}

	@GetMapping("/itemDto")
	Collection<ItemDto> getDtos() {
		return this.itemToDto(itemRepo.findAll());
	}

	private Collection<ItemDto> itemToDto(Collection<Item> items) {
		Collection<ItemDto> dtos = new HashSet<ItemDto>();
		for (Item item : items) {
			ItemDto dto = new ItemDto(item.getId(), item.getNumber(), item.getName(), item.getBrand().getName(), item.getCategory().getName(),
					item.getStatus(), item.getUnitsOnStack(), item.getUnitsScheduled());
			dtos.add(dto);
		}
		return dtos;
	}

	@GetMapping("/item/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Item> item = itemRepo.findById(id);
		return item.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/item/number/season/{season_id}")
	ResponseEntity<?> getAvailableNumberByCategory(@PathVariable Long season_id) {
		Season season = seasonRepo.findById(season_id).get();
		String prefix = season.getPrefix();
		Item item = itemRepo.getLast();
		Long item_id = (item == null ? 0L : item.getId()) + 1;
		if (item_id.toString().length() == 1) {
			prefix = prefix + "00";
		}
		if (item_id.toString().length() == 2) {
			prefix = prefix + "0";
		}
		String number = prefix + item_id.toString();
		return ResponseEntity.ok().body(Collections.singletonMap("number", number));
	}

	@GetMapping("/item/purchase/{purchase_id}")
	Collection<ItemDto> getAll(@PathVariable Long purchase_id) {
		return this.itemToDto(itemRepo.getPurchaseItems(purchase_id));
	}

	@GetMapping("/item/eta/{date}")
	Collection<ItemDto> getAllByEta(@PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") Date date, @RequestParam(name="negativeOnly", required=false) boolean negativeOnly) {
		Collection<ItemDto> dtos = new HashSet<ItemDto>();
		Map<Long, Long> pastComponents = new HashMap<Long, Long>();
		Map<Long, Long> futureComponents = new HashMap<Long, Long>();

		List<KeyValueDto> pc = receivingRepo.findReceivingPastEta(date);
		List<KeyValueDto> fc = receivingRepo.findReceivingFutureEta(date);
		
		for(KeyValueDto keyValueDto : pc) {
			pastComponents.put(keyValueDto.getKey(), (Long) keyValueDto.getValue());
		}
		for(KeyValueDto keyValueDto : fc) {
			futureComponents.put(keyValueDto.getKey(), (Long) keyValueDto.getValue());
		}

		
		for(Item item : itemRepo.findAll()) {
			ItemDto dto = new ItemDto();
			dto.setId(item.getId());
			dto.setNumber(item.getNumber());
			dto.setUnitsScheduled(item.getUnitsScheduled()==null?0:item.getUnitsScheduled().intValue());
			int itemsPastTransit = 0;
			int itemsFutureTransit = 0;
//			int itemsInTransit = 0;
			for(ItemComponent ic : item.getItemComponents()) {
				//Transit
				Long pastCompUnits = pastComponents.get(ic.getComponent().getId());
				Long futureCompUnits = futureComponents.get(ic.getComponent().getId());

//				int componentsInTransit = (int) (pastCompUnits==null?0:pastCompUnits);
//				float currentItemsInTransitFloat = componentsInTransit/ic.getUnits();
//				int currentItemsInTransit = 0;
				//Ready
//				int componentUnitsReserved = ic.getComponent().getUnitsReserved()==null?0:ic.getComponent().getUnitsReserved().intValue();
//				int componentUnitsAvailable = ic.getComponent().getUnitsOnStack();
				float currentItemsPastFloat = (ic.getComponent().getUnitsOnStack() + (pastCompUnits==null?0:pastCompUnits))/ic.getUnits();
				int currentItemsPast = 0;

				float currentItemsFutureFloat = (futureCompUnits==null?0:futureCompUnits)/ic.getUnits();
				int currentItemsFuture = 0;

				
				//Transit
//				if(currentItemsInTransitFloat>0) {
//					currentItemsInTransit = new BigDecimal(currentItemsInTransitFloat).setScale(0, RoundingMode.DOWN).intValue();
//				}else {
//					currentItemsInTransit = new BigDecimal(currentItemsInTransitFloat).setScale(0, RoundingMode.UP).intValue();
//				}
//				if(itemsInTransit == 0 || currentItemsInTransit < itemsInTransit) {
//					itemsInTransit = currentItemsInTransit;
//				}
				//Past
				if(currentItemsPastFloat>0) {
					currentItemsPast = new BigDecimal(currentItemsPastFloat).setScale(0, RoundingMode.DOWN).intValue();
				}else {
					currentItemsPast = new BigDecimal(currentItemsPastFloat).setScale(0, RoundingMode.UP).intValue();
				}
				if(itemsPastTransit == 0 || currentItemsPast < itemsPastTransit) {
					itemsPastTransit = currentItemsPast;
				}
				//Future
				if(currentItemsFutureFloat>0) {
					currentItemsFuture = new BigDecimal(currentItemsFutureFloat).setScale(0, RoundingMode.DOWN).intValue();
				}else {
					currentItemsFuture = new BigDecimal(currentItemsFutureFloat).setScale(0, RoundingMode.UP).intValue();
				}
				if(itemsFutureTransit == 0 || currentItemsFuture < itemsFutureTransit) {
					itemsFutureTransit = currentItemsFuture;
				}

			}
			dto.setUnitsReady(itemsPastTransit);
			dto.setUnitsPastTransit(itemsPastTransit);
			dto.setUnitsFutureTransit(itemsFutureTransit);
//			dto.setUnitsInTransit(itemsInTransit);
			if(negativeOnly) {
				if(itemsPastTransit<0) {
					dtos.add(dto);
				}
			}else {
				dtos.add(dto);
			}


		}
		return dtos;
	}

	/*
	 * Use this to create and update entity. No need to use PUT for update. If ID is
	 * not null, it will try to update.
	 */
	@PostMapping("/item")
	ResponseEntity<Item> post(@RequestBody(required = false) Item jsonItem) throws URISyntaxException {
		if (jsonItem == null) {
			jsonItem = new Item();
			Upc upc = upcRepo.getFirstAvailable();
			Upc caseUpc = upcRepo.getFirstAvailable();
			jsonItem.setUpc(upc);
			jsonItem.setCaseUpc(caseUpc);
		}
		for (ItemComponent ic : jsonItem.getItemComponents()) {
			if (ic.getId() != null) {
				ic.setItem(jsonItem);
			}
		}
		Item result = itemRepo.save(jsonItem);
		return ResponseEntity.ok().body(result);
	}

	// This includes image upload.
	@PostMapping("/item/upload")
	ResponseEntity<Item> postItemAndAttachment(@RequestParam(value = "image", required = false) MultipartFile image, @RequestParam("jsonItem") String jsonItem)
			throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		Item item = objectMapper.readValue(jsonItem, Item.class);
		for (ItemComponent ic : item.getItemComponents()) {
			if (ic.getId() != null) {
				ic.setItem(item);
			}
		}
		if (image != null) {
			Attachment attachment = new Attachment();
			attachment.setData(image.getBytes());
			attachmentRepo.save(attachment);
			item.setAttachment(attachment);
		}
		Item result = itemRepo.save(item);
		return ResponseEntity.created(new URI("/api/item/" + result.getId())).body(result);
	}

	@DeleteMapping("/item/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		itemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}