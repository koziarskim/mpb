package com.noovitec.mpb.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.NotificationService;
import com.noovitec.mpb.service.SaleService;
import com.noovitec.mpb.service.ScheduleEventService;

@RestController
@RequestMapping("/api")
class ScheduleEventRest {

	private final Logger log = LoggerFactory.getLogger(ScheduleEventRest.class);
	private ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SaleService saleService;
	@Autowired
	private ScheduleEventService scheduleEventService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private CrudService crudService;

	public ScheduleEventRest(ScheduleEventRepo scheduleEventRepo) {
		this.scheduleEventRepo = scheduleEventRepo;
	}

	@GetMapping("/scheduleEvent")
	Collection<ScheduleEvent> getAll() {
		return scheduleEventRepo.findAll();
	}

	@GetMapping("/scheduleEvent/{id}")
	ResponseEntity<ScheduleEvent> get(@PathVariable Long id) {
		Optional<ScheduleEvent> result = scheduleEventRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/scheduleEvent/date/{date}/line/{line_id}")
	List<ScheduleEvent> getByLine(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @PathVariable Long line_id) {
		List<ScheduleEvent> result = scheduleEventRepo.findByDateAndLine(date, line_id);
		List<ScheduleEvent> sorted = result.stream().sorted((se1, se2) -> se1.getScheduleTime().compareTo(se2.getScheduleTime())).collect(Collectors.toList());;
		return sorted;
	}

	@GetMapping("/scheduleEvent/item/{item_id}")
	List<ScheduleEvent> getByItem(@PathVariable Long item_id) {
		List<ScheduleEvent> result = scheduleEventRepo.findByItem(item_id);
		List<ScheduleEvent> sorted = result.stream().sorted((se1, se2) -> se1.getSchedule().getDate().compareTo(se2.getSchedule().getDate())).collect(Collectors.toList());;
		return sorted;
	}

	@PostMapping("/scheduleEvent")
	ResponseEntity<ScheduleEvent> post(@RequestBody ScheduleEvent scheduleEvent) {
		boolean isNew = scheduleEvent.getId()==null;
		scheduleEvent = (ScheduleEvent) crudService.merge(scheduleEvent);
		if(isNew) {
			List<String> emails = new ArrayList<String>();
			emails.add("dramirez@marketplacebrands.com");
			emails.add("evazquez@marketplacebrands.com");
			Map<String, String> model = new HashMap<String, String>();
			String itemNumber = scheduleEvent.getSaleItem().getItem().getNumber() + " " + scheduleEvent.getSaleItem().getItem().getName();
			String saleNumber = scheduleEvent.getSaleItem().getSale().getNumber();
			String customerName = scheduleEvent.getSaleItem().getSale().getCustomer().getName();
			model.put("itemNumber", itemNumber);
			model.put("saleNumber", saleNumber);
			model.put("customerName", customerName);
			notificationService.sendMail(emails, model, Notification.TYPE.SCHEDULE_CREATED);
		}
		if(scheduleEvent.getFinishTime()!=null) {
			List<String> emails = new ArrayList<String>();
			emails.add("kzygulska@marketplacebrands.com");
			Map<String, String> model = new HashMap<String, String>();
			String itemNumber = scheduleEvent.getSaleItem().getItem().getNumber()+" "+scheduleEvent.getSaleItem().getItem().getName();
			String saleNumber = scheduleEvent.getSaleItem().getSale().getNumber();
			model.put("itemNumber", itemNumber);
			model.put("saleNumber", saleNumber);
			notificationService.sendMail(emails, model, Notification.TYPE.PRODUCTION_COMPLETED);
		}
		for (Production production : scheduleEvent.getProductions()) {
			production.setScheduleEvent(scheduleEvent);
			Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
			componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff);
		}
		scheduleEvent = scheduleEventService.save(scheduleEvent);
		itemService.updateUnits(Arrays.asList(scheduleEvent.getSaleItem().getItem().getId()));
		saleService.updateUnits(Arrays.asList(scheduleEvent.getSaleItem().getSale().getId()));
		componentService.updateUnitsLockedByItem(scheduleEvent.getSaleItem().getItem().getId());
		itemService.updateUnitsReadyProd(Arrays.asList(scheduleEvent.getSaleItem().getItem().getId()));
		return ResponseEntity.ok(scheduleEvent);
	}

	@DeleteMapping("/scheduleEvent/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(id);
		for (Production production : scheduleEvent.getProductions()) {
			Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
			componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff * (-1));
		}
		Long itemId = scheduleEvent.getSaleItem().getItem().getId();
		Long saleId = scheduleEvent.getSaleItem().getSale().getId();
		scheduleEventService.delete(id);
		itemService.updateUnits(Arrays.asList(itemId));
		saleService.updateUnits(Arrays.asList(saleId));
		componentService.updateUnitsLockedByItem(itemId);
		itemService.updateUnitsReadyProd(Arrays.asList(itemId));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/scheduleEvent/migrate")
	ResponseEntity<?> migrate(){
		List<ScheduleEvent> scheduleEvents = scheduleEventRepo.findAll();
		for(ScheduleEvent se: scheduleEvents) {
			SaleItem si = se.getSaleItem();
			se.setItem(si.getItem());
			scheduleEventRepo.save(se);
		}
		log.info("done");
		return ResponseEntity.ok().build();
	}

}