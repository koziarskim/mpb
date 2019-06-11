package com.noovitec.mpb.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.noovitec.mpb.dto.ItemAvailabilityDto;
import com.noovitec.mpb.dto.ItemDto;
import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.projection.ItemAvailabilityProjection;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
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

	private final Logger log = LoggerFactory.getLogger(ItemRest.class);
	private ItemRepo itemRepo;

	public ItemRest(ItemRepo itemRepo) {
		this.itemRepo = itemRepo;
	}

	@GetMapping("/item")
	Collection<Item> getAll() {
		return itemRepo.findAll();
	}

	@GetMapping("/itemListDto")
	Collection<ItemListDto> getDtos() {
		Collection<ItemListDto> dtos = new HashSet<ItemListDto>();
		for (Item item : itemRepo.findAll()) {
			ItemListDto dto = new ItemListDto();
			dto.setId(item.getId());
			dto.setNumber(item.getNumber());
			dto.setName(item.getName());
			dto.setBrand(item.getBrand()==null?null:item.getBrand().getName());
			dto.setCategory(item.getCategory()==null?null:item.getCategory().getName());
			dto.setUnitsOnStack(item.getUnitsOnStack()==null?0L:item.getUnitsOnStack());
			dto.setUnitsSold(0L);
			dto.setUnitsScheduled(item.getUnitsScheduled()==null?0L:item.getUnitsScheduled());
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
		Collection<ItemDto> dtos = new HashSet<ItemDto>();
		for (Item item : itemRepo.getPurchaseItems(purchase_id)) {
			ItemDto dto = new ItemDto(item.getId(), item.getNumber(), item.getName(), item.getBrand() == null ? null : item.getBrand().getName(),
					item.getCategory() == null ? null : item.getCategory().getName(), item.getStatus(), item.getUnitsOnStack(), item.getUnitsScheduled());
			dtos.add(dto);
		}
		return dtos;
	}

	@GetMapping("/item/available/eta/{date}")
	Collection<ItemAvailabilityDto> getAvailableItems(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			@RequestParam(name = "itemIds", required = false) List<Long> itemIds) {
		if (itemIds == null) {
			itemIds = new ArrayList<Long>();
			itemIds.add(0L);
		}
		List<ItemAvailabilityDto> dtos = new ArrayList<ItemAvailabilityDto>();
		for(ItemAvailabilityProjection iap : itemRepo.getItemsAvailabilityFiltered(date, itemIds)) {
			ItemAvailabilityDto dto = new ItemAvailabilityDto();
			dto.setId(iap.getId());
			dto.setUnitsToProduction(iap.getUnitsToProduction());
			Long itemUnits = itemRepo.getItemsScheduledToDate(date, iap.getId());
			if(itemUnits==null) {
				itemUnits = 0L;
			}
			dto.setUnitsScheduled(itemUnits);
			dto.setUnitsToSchedule(iap.getUnitsToSchedule() - itemUnits);
			dtos.add(dto);
		}
		return dtos;
	}

	@GetMapping("/item/{item_id}/eta/{date}")
	Collection<ItemDto> getItemByEta(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable Long item_id) {
		return this.getByEta(date, item_id, true);
	}

	@GetMapping("/item/eta/{date}")
	Collection<ItemDto> getAllByEta(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
			@RequestParam(name = "includeAll", required = false) boolean includeAll) {
		return this.getByEta(date, null, includeAll);
	}

	private Collection<ItemDto> getByEta(Date date, Long item_id, boolean includeAll) {
		Collection<ItemDto> dtos = new HashSet<ItemDto>();
		Map<Long, Long> componentsInTransitToDate = new HashMap<Long, Long>();

		List<KeyValueDto> componentsInTransitToDateList = receivingRepo.findComponentsInTransitToDate(date);

		for (KeyValueDto keyValueDto : componentsInTransitToDateList) {
			componentsInTransitToDate.put(keyValueDto.getKey(), (Long) keyValueDto.getValue());
		}

		Collection<Item> items = new HashSet<Item>();
		if (item_id == null) {
			items.addAll(itemRepo.findAll());
		} else {
			items.add(itemRepo.getOne(item_id));
		}

		for (Item item : items) {
			ItemDto dto = new ItemDto();
			dto.setId(item.getId());
			dto.setNumber(item.getNumber());
			int itemsReadySchedule = 0;
			int itemsReadyProduction = 0;
			for (ItemComponent ic : item.getItemComponents()) {
				Long componentUnitsInTransit = componentsInTransitToDate.get(ic.getComponent().getId());

				float icReadyProductionFloat = ic.getComponent().getUnitsOnStack() / ic.getUnits();
				float icReadyScheduleFloat = (ic.getComponent().getUnitsOnStack() + (componentUnitsInTransit == null ? 0 : componentUnitsInTransit))
						/ ic.getUnits();
				// Production
				int icReadyProduction = this.roundToInt(icReadyProductionFloat);
				if (itemsReadyProduction == 0 || icReadyProduction < itemsReadyProduction) {
					itemsReadyProduction = icReadyProduction;
				}
				// Schedule
				int icReadySchedule = this.roundToInt(icReadyScheduleFloat);
				if (itemsReadySchedule == 0 || icReadySchedule < itemsReadySchedule) {
					itemsReadySchedule = icReadySchedule;
				}
			}
			Long totalItemSchedule = scheduleEventRepo.getTotalItemScheduled(LocalDate.now(), item.getId());
			dto.setTotalItemScheduled(totalItemSchedule == null ? 0 : totalItemSchedule.intValue());
			dto.setUnitsReadySchedule(itemsReadySchedule);
			dto.setUnitsReadyProduction(itemsReadyProduction);
			if (!includeAll) {
				if (dto.getUnitsReadySchedule() > 0) {
					dtos.add(dto);
				}
			} else {
				dtos.add(dto);
			}
		}
		return dtos;
	}

	private int roundToInt(float unitsFloat) {
		int units = 0;
		if (unitsFloat > 0) {
			units = new BigDecimal(unitsFloat).setScale(0, RoundingMode.DOWN).intValue();
		} else {
			units = new BigDecimal(unitsFloat).setScale(0, RoundingMode.UP).intValue();
		}
		return units;
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