package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.ScheduleEventListDto;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Production;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
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
	
	@GetMapping("/scheduleEvent/pageable")
	Page<?> getAllPageable(
			@RequestParam(required = true) Pageable pageable, 
			@RequestParam(required = false) boolean totals,
			@RequestParam(required = false) Long saleId,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) Long packagingId) {
		if(totals) {
			Page<?> resultTotals = scheduleEventRepo.findPageable(pageable, totals, saleId, itemId, packagingId);
			return resultTotals;
		}
		@SuppressWarnings("unchecked")
		Page<ScheduleEvent> scheduleEvents = (Page<ScheduleEvent>) scheduleEventRepo.findPageable(pageable, totals, saleId, itemId, packagingId);
		Page<ScheduleEventListDto> all = scheduleEvents.map(se -> {
			ScheduleEventListDto dto = new ScheduleEventListDto();
			dto.setId(se.getId());
			dto.setSaleId(se.getSaleItem()!=null?se.getSaleItem().getSale().getId():null);
			dto.setSaleNumber(se.getSaleItem()!=null?se.getSaleItem().getSale().getNumber():"---");
			dto.setSaleItemId(se.getSaleItem()!=null?se.getSaleItem().getId():null);
			dto.setItemId(se.getItemPackaging().getItem().getId());
			dto.setItemName(se.getItemPackaging().getItem().getName());
			dto.setItemNumber(se.getItemPackaging().getItem().getNumber());
			dto.setLineId(se.getLine().getId());
			dto.setPackagingId(se.getItemPackaging().getPackaging().getId());
			dto.setPackagingLabel(se.getItemPackaging().getLabel());
			dto.setScheduleDate(se.getDate());
			dto.setStartTime(se.getStartTime());
			dto.setFinishTime(se.getFinishTime());
			dto.setUnitsScheduled(se.getUnitsScheduled());
			dto.setUnitsProduced(se.getUnitsProduced());
			return dto;
		});
		return all;
	}


	@GetMapping("/scheduleEvent/date/{date}")
	List<ScheduleEvent> getByLine(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @RequestParam(required = false) Long line_id) {
		List<ScheduleEvent> result = null;
		if(line_id==null) {
			result = scheduleEventRepo.findByDate(date);
		}else {
			result = scheduleEventRepo.findByDateAndLine(date, line_id);
		}
		List<ScheduleEvent> sorted = result.stream().sorted((se1, se2) -> se1.getScheduleTime().compareTo(se2.getScheduleTime())).collect(Collectors.toList());;
		return sorted;
	}

	@GetMapping("/scheduleEvent/item/{item_id}")
	List<ScheduleEvent> getByItem(@PathVariable Long item_id) {
		List<ScheduleEvent> result = scheduleEventRepo.findByItem(item_id);
		List<ScheduleEvent> sorted = result.stream().sorted((se1, se2) -> se1.getDate().compareTo(se2.getDate())).collect(Collectors.toList());;
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
			String itemNumber = scheduleEvent.getItemPackaging().getItem().getNumber() + " " + scheduleEvent.getItemPackaging().getItem().getName();
			String packagingLabel = scheduleEvent.getItemPackaging().getLabel();
			String saleNumber = "None";
			String customerName = "None";
			if(scheduleEvent.getSaleItem()!=null) {
				saleNumber = scheduleEvent.getSaleItem().getSale().getNumber();
				customerName = scheduleEvent.getSaleItem().getSale().getCustomer().getName();
			}
			model.put("itemNumber", itemNumber);
			model.put("packagingLabel", packagingLabel);
			model.put("saleNumber", saleNumber);
			model.put("customerName", customerName);
			notificationService.sendMail(emails, model, Notification.TYPE.SCHEDULE_CREATED);
		}
		if(scheduleEvent.getFinishTime()!=null) {
			List<String> emails = new ArrayList<String>();
			emails.add("kzygulska@marketplacebrands.com");
			Map<String, String> model = new HashMap<String, String>();
			String itemNumber = scheduleEvent.getItemPackaging().getItem().getNumber()+" "+scheduleEvent.getItemPackaging().getItem().getName();
			String saleNumber = "None";
			if(scheduleEvent.getSaleItem() != null) {
				saleNumber = scheduleEvent.getSaleItem().getSale().getNumber();
			}
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
		itemService.updateUnits(Arrays.asList(scheduleEvent.getItemPackaging().getItem().getId()));
		if(scheduleEvent.getSaleItem()!=null) {
			saleService.updateUnits(Arrays.asList(scheduleEvent.getSaleItem().getSale().getId()));
		}
		componentService.updateUnitsLockedByItem(scheduleEvent.getItemPackaging().getItem().getId());
		itemService.updateUnitsReadyProd(Arrays.asList(scheduleEvent.getItemPackaging().getItem().getId()));
		return ResponseEntity.ok(scheduleEvent);
	}

	@DeleteMapping("/scheduleEvent/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(id);
		for (Production production : scheduleEvent.getProductions()) {
			Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
			componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff * (-1));
		}
		Long itemId = scheduleEvent.getItemPackaging().getItem().getId();
		Long saleId = null;
		if(scheduleEvent.getSaleItem() != null) {
			saleId = scheduleEvent.getSaleItem().getSale().getId();
		}
		scheduleEventService.delete(id);
		itemService.updateUnits(Arrays.asList(itemId));
		saleService.updateUnits(Arrays.asList(saleId));
		componentService.updateUnitsLockedByItem(itemId);
		itemService.updateUnitsReadyProd(Arrays.asList(itemId));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/scheduleEvent/{id}/schedule/pdf")
	HttpEntity<byte[]> getTagPdf(
			@PathVariable Long id) throws DocumentException, IOException {
		ScheduleEvent scheduleEvent = scheduleEventRepo.findById(id).get();
		String scheduleDate = scheduleEvent.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
		String fileName = "Schedule_"+scheduleEvent.getSaleItem().getItemPackaging().getItem().getNumber()+"_"+scheduleDate+".pdf";
		byte[] data = this.generateSchedulePdf(scheduleEvent);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	private byte[] generateSchedulePdf(ScheduleEvent scheduleEvent) throws IOException, DocumentException {
		InputStream bolIn = this.getClass().getClassLoader().getResourceAsStream("pdf/Prod-Schedule.pdf");
		PdfReader bolTemplate = new PdfReader(bolIn);
		ByteArrayOutputStream bolBaos = new ByteArrayOutputStream();
		PdfStamper bolStamper = new PdfStamper(bolTemplate, bolBaos);
		bolStamper.setFormFlattening(true);
		bolStamper.getAcroFields().setField("totalPallets", "");
		bolStamper.close();
		bolTemplate.close();
		return bolBaos.toByteArray();
	}
	
	
}