package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.ItemTreeDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.ScheduleEventTreeDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.entity.Season;
import com.noovitec.mpb.entity.Upc;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ReceivingRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.repo.SeasonRepo;
import com.noovitec.mpb.repo.UpcRepo;

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
	@Autowired
	ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private EntityManager entityManager;

	private final Logger log = LoggerFactory.getLogger(ItemRest.class);
	private ItemRepo itemRepo;

	public ItemRest(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@GetMapping("/item")
	Collection<Item> getAll() {
		return (Collection<Item>) itemRepo.findAll();
	}

	@GetMapping("/itemListDto")
	Collection<ItemListDto> getDtos() {
		return itemRepo.getItemListDto();
	}

	@GetMapping("/item/kv")
	Collection<KeyValueDto> getAllKeyValueDtos() {
		return itemRepo.getAllKeyValueDtos();
	}

	@GetMapping("/item/pageable")
	Page<ItemListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, 
			@RequestParam(name = "searchKey", required = false) String searchKey,
			@RequestParam(name = "searchType", required = false) String searchType) {
		Page<Item> items = null;
		if(searchType==null || searchType.isBlank() || searchKey==null || searchKey.isBlank()) {
			items = itemRepo.findPage(pageable);
		}else if(searchType.equals("item") && !searchKey.isBlank()) {
			items = itemRepo.findPageByItem(pageable, searchKey);
		}else if(searchType.equals("component") && !searchKey.isBlank()){
			items = itemRepo.findPageByComponent(pageable, searchKey);
		}
		if(items == null) {
			 return Page.empty();
		}
		Page<ItemListDto> dtos = items.map(item -> {
			ItemListDto dto = new ItemListDto();
			dto.setId(item.getId());
			dto.setNumber(item.getNumber());
			dto.setName(item.getName());
			dto.setBrand(item.getBrand()==null?"":item.getBrand().getName());
			dto.setCategory(item.getCategory()==null?"":item.getCategory().getName());
			dto.setUnitsOnStock(item.getUnitsOnStock());
			dto.setUnitsSold(item.getUnitsSold());
			dto.setUnitsScheduled(item.getUnitsScheduled());
			dto.setUnitsProduced(item.getUnitsProduced());
			dto.setUnitsShipped(item.getUnitsShipped());
		    return dto;
		});
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

	@GetMapping("/item/production/date/{date}")
	Collection<ItemTreeDto> getItemsByDate(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		List<ItemTreeDto> dtos = new ArrayList<ItemTreeDto>();
		List<ScheduleEvent> events = scheduleEventRepo.findByDate(date);
		for(ScheduleEvent se: events) {
			ItemTreeDto itemDto = dtos.stream().filter(existingDto -> existingDto.getId().equals(se.getSaleItem().getItem().getId())).findAny().orElse(null);
			if(itemDto == null) {
				itemDto = new ItemTreeDto();
				itemDto.setId(se.getSaleItem().getItem().getId());
				itemDto.setName(se.getSaleItem().getItem().getName());
				itemDto.setUnitsOnStock(se.getSaleItem().getItem().getUnitsOnStock());
				itemDto.setTotalSold(se.getSaleItem().getItem().getUnitsSold());
				itemDto.setTotalProduced(se.getSaleItem().getItem().getUnitsProduced());
				itemDto.setTotalSeconds(se.getSaleItem().getItem().getDurationSeconds());
				dtos.add(itemDto);
			}
			itemDto.setDailyScheduled(itemDto.getDailyScheduled()+se.getUnitsScheduled());
			itemDto.setDailyProduced(itemDto.getDailyProduced()+se.getTotalProduced());
			itemDto.setDailySeconds(itemDto.getDailySeconds()+se.getDurationSeconds());
			ScheduleEventTreeDto eventDto = new ScheduleEventTreeDto();
			eventDto.setId(se.getId());
			eventDto.setCustomerName(se.getSaleItem().getSale().getCustomer().getName());
			eventDto.setSaleNumber(se.getSaleItem().getSale().getNumber());
			eventDto.setLineNumber(String.valueOf(se.getLine().getNumber()));
			eventDto.setUnitsSold(se.getSaleItem().getSale().getUnitsSold());
			eventDto.setSaleTotalProduced(se.getSaleItem().getSale().getUnitsProduced());
			eventDto.setDailyScheduled(se.getUnitsScheduled());
			eventDto.setDailyProduced(se.getTotalProduced());
			eventDto.setDailySeconds(se.getDurationSeconds());
			itemDto.getEvents().add(eventDto);
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