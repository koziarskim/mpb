package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.ItemDto;
import com.noovitec.mpb.dto.ItemListDto;
import com.noovitec.mpb.dto.ItemTreeDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.ScheduleEventTreeDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.ItemPackaging;
import com.noovitec.mpb.entity.Packaging;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.entity.Season;
import com.noovitec.mpb.repo.ItemPackagingRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.PackagingRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.repo.SeasonRepo;
import com.noovitec.mpb.repo.UpcRepo;
import com.noovitec.mpb.service.AttachmentService;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.CustomerService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.PurchaseService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class ItemRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SeasonRepo seasonRepo;
	@Autowired
	UpcRepo upcRepo;
	@Autowired
	ScheduleEventRepo scheduleEventRepo;
	@Autowired
	private ItemRepo itemRepo;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private SaleService saleService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private PackagingRepo packagingRepo;
	@Autowired
	private CrudService crudService;
	@Autowired
	private ItemPackagingRepo itemPackagingRepo;
	
	private ItemService itemService;
	private final Logger log = LoggerFactory.getLogger(ItemRest.class);
	
	public ItemRest(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping("/item")
	Collection<Item> getAll() {
		return (Collection<Item>) itemRepo.findAll();
	}
	
	@GetMapping("/item/{id}/dto")
	ItemDto getDto(@PathVariable Long id) {
		Item item = itemRepo.findById(id).get();
		ItemDto dto = new ItemDto();
		dto.setId(item.getId());
		dto.setBrandName(item.getBrand().getName());
		dto.setCategoryName(item.getCategory().getName());
		dto.setName(item.getName());
		dto.setNumber(item.getNumber());
		dto.setUnitsAdjusted(item.getUnitsAdjusted());
		dto.setUnitsOnStock(item.getUnitsOnStock());
		dto.setUnitsProduced(item.getUnitsProduced());
		dto.setUnitsScheduled(item.getUnitsScheduled());
		dto.setUnitsShipped(item.getUnitsShipped());
		dto.setUnitsSold(item.getUnitsSold());
		return dto;
	}

	@GetMapping("/item/kv")
	Collection<KeyValueDto> getAllKeyValueDtos() {
		return itemRepo.getAllKeyValueDtos();
	}

	@GetMapping("/item/pageable")
	Page<ItemListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable,
			@RequestParam(required = false) String numberName, 
			@RequestParam(required = false) Long componentId,
			@RequestParam(required = false) Long brandId,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String unitsFilter) {
		Page<Item> items = itemRepo.findPagable(pageable, numberName, componentId, brandId, categoryId, unitsFilter);
		Page<ItemListDto> dtos = items.map(item -> {
			ItemListDto dto = new ItemListDto();
			dto.setId(item.getId());
			dto.setNumber(item.getNumber());
			dto.setName(item.getName());
			dto.setBrand(item.getBrand() == null ? "" : item.getBrand().getName());
			dto.setCategory(item.getCategory() == null ? "" : item.getCategory().getName());
			dto.setUnitsOnStock(item.getUnitsOnStock());
			dto.setUnitsSold(item.getUnitsSold());
			dto.setSalesNotAssigned(item.getSalesNotAssigned());
			dto.setUnitsScheduled(item.getUnitsScheduled());
			dto.setUnitsProduced(item.getUnitsProduced());
			dto.setUnitsShipped(item.getUnitsShipped());
			dto.setUnitsAdjusted(item.getUnitsAdjusted());
			dto.setUnitsOnFloor(item.getUnitsProduced() - item.getUnitsShipped());
			dto.setUnitsNotAssigned(item.getUnitsNotAssigned());
			dto.setUnitsShort(item.getUnitsShort());
			dto.setNumPackagings(item.getItemPackagings().size());
			return dto;
		});
		return dtos;
	}

	@GetMapping("/item/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Item item = itemRepo.findById(id).get();
		return ResponseEntity.ok().body(item);
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
		for (ScheduleEvent se : events) {
			ItemTreeDto itemDto = dtos.stream().filter(existingDto -> existingDto.getId().equals(se.getItemPackaging().getItem().getId())).findAny().orElse(null);
			if (itemDto == null) {
				itemDto = new ItemTreeDto();
				itemDto.setId(se.getItemPackaging().getItem().getId());
				itemDto.setName(se.getItemPackaging().getItem().getName());
				itemDto.setUnitsOnStock(se.getItemPackaging().getItem().getUnitsOnStock());
				itemDto.setTotalSold(se.getItemPackaging().getItem().getUnitsSold());
				itemDto.setTotalProduced(se.getItemPackaging().getItem().getUnitsProduced());
				itemDto.setTotalSeconds(se.getItemPackaging().getItem().getDurationSeconds());
				dtos.add(itemDto);
			}
			itemDto.setDailyScheduled(itemDto.getDailyScheduled() + se.getUnitsScheduled());
			itemDto.setDailyProduced(itemDto.getDailyProduced() + se.getUnitsProduced());
			itemDto.setDailySeconds(itemDto.getDailySeconds() + se.getDurationSeconds());
			ScheduleEventTreeDto eventDto = new ScheduleEventTreeDto();
			eventDto.setId(se.getId());
			if(se.getSaleItem()!=null) {
				eventDto.setCustomerName(se.getSaleItem().getSale().getCustomer().getName());
				eventDto.setSaleNumber(se.getSaleItem().getSale().getNumber());
				eventDto.setUnitsSold(se.getSaleItem().getSale().getUnitsSold());
				eventDto.setSaleTotalProduced(se.getSaleItem().getSale().getUnitsProduced());
			}
			eventDto.setLineNumber(String.valueOf(se.getLine().getNumber()));
			eventDto.setDailyScheduled(se.getUnitsScheduled());
			eventDto.setDailyProduced(se.getUnitsProduced());
			eventDto.setDailySeconds(se.getDurationSeconds());
			eventDto.setDailyPeople(se.getTotalPeople());
			if(eventDto.getDailyPeople() > itemDto.getTotalPeople()) {
				itemDto.setTotalPeople(eventDto.getDailyPeople());
			}
			itemDto.getEvents().add(eventDto);
		}
		return dtos;
	}

	@PostMapping("/item")
	ResponseEntity<?> postItemAndAttachment(@RequestParam(required = false) MultipartFile image, @RequestParam String jsonItem) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		Item item = objectMapper.readValue(jsonItem, Item.class);
		for(ItemComponent ic: item.getItemComponents()) {
			ic.setItem(item);
		}
		for(ItemPackaging ip: item.getItemPackagings()) {
			ip.setItem(item);
		}
		
		if(!item.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Component Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = itemRepo.getIdByNumber(item.getNumber());
		if((item.getId()==null && id !=null) || (item.getId()!=null && id !=null && !item.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Component Number already exists. Please, choose differrent.");
		}
		item = itemRepo.save(item);
		if(image!=null) {
			Attachment attachment = attachmentService.store(image, Item.class.getSimpleName(), item.getId(), item.getAttachment());
			item.setAttachment(attachment);
			item = itemService.save(item);
		}
		itemService.updateUnits(Arrays.asList(item.getId()));
		List<Long> componentIds = new ArrayList<Long>();
		for (ItemComponent ic : item.getItemComponents()) {
			componentIds.add(ic.getComponent().getId());
		}
		componentService.updateUnits(componentIds);
		itemService.updateUnitsReadyProd(Arrays.asList(item.getId()));
		return ResponseEntity.ok(item);
	}

	@DeleteMapping("/item/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Item item = itemRepo.findById(id).get();
//		if(item.getSaleItems().size()>0) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Sales!");
//		}
//		if(item.getItemReturns().size()>0) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Returns!");
//		}
		itemService.delete(id);
		return ResponseEntity.ok().build();
	}

	//----Triggers----

	@GetMapping("/item/update/units/async")
	ResponseEntity<?> postUpdateAsync() {
		log.info("API Called");
		itemService.asyncUpdateUnits();
		log.info("API Return");
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping("/item/update/units")
	ResponseEntity<?> postUpdateUnits() {
		try {
			itemService.updateUnits(null);
			componentService.updateUnits(null);
			saleService.updateUnits(null);
			itemService.updateUnitsReadyProd(null);
			purchaseService.updateUnits(null);
			log.info("Done updating item units!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}
	
	@GetMapping("/item/{id}/checklist/pdf")
	HttpEntity<byte[]> getChecklistPdf(@PathVariable Long id) throws DocumentException, IOException {
		Item item = itemRepo.findById(id).get();
		String fileName = "Checklist_"+item.getNumber()+".pdf";
		byte[] data = this.generateChecklistPdf(item);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}
	
	private byte[] generateChecklistPdf(Item item) throws IOException, DocumentException {
		List<SaleItem> saleItems = itemService.findSaleItemsForChecklist(item.getId());
		if(saleItems.size()==0) {
			InputStream emptyIs = this.getClass().getClassLoader().getResourceAsStream("pdf/Empty.pdf");
			return emptyIs.readAllBytes();
		}
	    Document doc = new Document();
	    ByteArrayOutputStream mainBaos = new ByteArrayOutputStream();
	    PdfSmartCopy copy = new PdfSmartCopy(doc, mainBaos);
	    doc.open();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("pdf/Checklist.pdf");
		PdfReader mainReader = new PdfReader(is);
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		int customerCount = 0;
		int customersPerPage = 7;
		Map<Customer, List<SaleItem>> customerMap = new HashMap<Customer, List<SaleItem>>();
		for(SaleItem saleItem: saleItems) {
			Customer customer = saleItem.getSale().getCustomer();
			List<SaleItem> customerSaleItems = customerMap.get(customer);
			if(customerSaleItems == null) {
				customerSaleItems = new ArrayList<SaleItem>();
			}
			customerSaleItems.add(saleItem);
			customerMap.put(customer, customerSaleItems);
		}
		int pages = (int) Math.ceil((double) customerMap.size()/customersPerPage);
		for (int i=0; i<pages; i++) {
	    	PdfReader reader = new PdfReader(mainReader);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfStamper stamper = new PdfStamper(reader, baos);
	        stamper.getAcroFields().setField("itemNumber", item.getNumber()+" - "+item.getName());
	        for (int c=1; c<=customersPerPage; c++) {
	        	if(customerCount<customerMap.size()) {
			        Customer customer = (Customer) customerMap.keySet().toArray()[customerCount];
			        String customerName = customer.getName();
			        stamper.getAcroFields().setField("customerName"+c, customerName);
			        stamper.getAcroFields().setField("priceSticker"+c, customer.isPriceTicket()?"Yes":"No");
			        String cartonLabelType = "---";
			        if(customer.isCartonLabel()) {
			        	cartonLabelType = customer.isEdi()?"EDI":"MIMS";
			        }
			        stamper.getAcroFields().setField("cartonLabelType"+c, cartonLabelType);
			        stamper.getAcroFields().setField("palletTagType"+c,  customer.getPalletTagType());
			        long totalUnits = 0;
			        long totalCases = 0;
			        long totalPallets = 0;
			        String cartonTypes = "";
			        for(SaleItem si: customerMap.get(customer)) {
			        	long units = (si.getUnits() + si.getUnitsAdjusted() - si.getUnitsAssigned());
			        	totalUnits += units;
						long cases = (long) Math.ceil((double) units/si.getItemPackaging().getPackaging().getCasePack());
						totalCases += cases;
						long pallets = (long) Math.ceil((double) cases/(si.getItemPackaging().getPackaging().getHi()*si.getItemPackaging().getPackaging().getTi()));
						totalPallets += pallets;
						String cartonType = Packaging.TYPE.valueOf(si.getItemPackaging().getPackaging().getType()).label();
			        	if(cartonTypes.indexOf(cartonType) == -1) {
			        		cartonTypes += cartonType+"\n";
			        	}
			        }
			        stamper.getAcroFields().setField("totalUnits"+c, df.format(totalUnits));
			        stamper.getAcroFields().setField("totalCases"+c, df.format(totalCases));
			        stamper.getAcroFields().setField("totalPallets"+c, df.format(totalPallets));
			        stamper.getAcroFields().setField("cartonType"+c, cartonTypes);
			        customerCount++;
		        }
	        }
	        stamper.setFormFlattening(true);
	        stamper.close();
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	        reader.close();
	        baos.close();
	    };
	    doc.close();
	    return mainBaos.toByteArray();
	}


}