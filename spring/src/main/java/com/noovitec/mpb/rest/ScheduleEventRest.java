package com.noovitec.mpb.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.noovitec.mpb.dto.ScheduleEventListDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.entity.Packaging;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.service.AttachmentService;
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
	@Autowired
	private AttachmentService attachmentService;

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
//		for (Production production : scheduleEvent.getProductions()) {
//			production.setScheduleEvent(scheduleEvent);
//			Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
//			componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff);
//		}
		scheduleEvent = scheduleEventService.save(scheduleEvent);
		itemService.updateUnits(Arrays.asList(scheduleEvent.getItemPackaging().getItem().getId()));
		if(scheduleEvent.getSaleItem()!=null) {
			saleService.updateUnits(Arrays.asList(scheduleEvent.getSaleItem().getSale().getId()));
		}
		componentService.updateUnitsByItems(Arrays.asList(scheduleEvent.getItemPackaging().getItem().getId()));
		itemService.updateUnitsReadyProd(Arrays.asList(scheduleEvent.getItemPackaging().getItem().getId()));
		return ResponseEntity.ok(scheduleEvent);
	}

	@DeleteMapping("/scheduleEvent/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		ScheduleEvent scheduleEvent = scheduleEventRepo.getOne(id);
//		for (Production production : scheduleEvent.getProductions()) {
//			Long unitsDiff = production.getUnitsProduced() - production.getPreUnitsProduced();
//			componentService.updateUnitsOnStockByProduction(production.getId(), unitsDiff * (-1));
//		}
		Long itemId = scheduleEvent.getItemPackaging().getItem().getId();
		Long saleId = null;
		if(scheduleEvent.getSaleItem() != null) {
			saleId = scheduleEvent.getSaleItem().getSale().getId();
		}
		scheduleEventService.delete(id);
		itemService.updateUnits(Arrays.asList(itemId));
		saleService.updateUnits(Arrays.asList(saleId));
		componentService.updateUnitsByItems(Arrays.asList(itemId));
		itemService.updateUnitsReadyProd(Arrays.asList(itemId));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/scheduleEvent/{id}/schedule/pdf")
	HttpEntity<byte[]> getProdScheduleByIdPdf(@PathVariable Long id) throws DocumentException, IOException {
		ScheduleEvent scheduleEvent = scheduleEventRepo.findById(id).get();
		String scheduleDate = scheduleEvent.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
		String fileName = "Schedule_"+scheduleEvent.getItemPackaging().getItem().getNumber()+"_"+scheduleDate+".pdf";
		byte[] data = this.generateSchedulePdf(Arrays.asList(scheduleEvent, scheduleEvent));
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/scheduleEvent/date/{date}/schedule/pdf")
	HttpEntity<byte[]> getProdScheduleByDatePdf(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) throws DocumentException, IOException {
		List<ScheduleEvent> scheduleEvents = scheduleEventRepo.findByDate(date);
		String scheduleDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
		String fileName = "Schedule_"+scheduleDate+".pdf";
		byte[] data = this.generateSchedulePdf(scheduleEvents);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

    public void doMerge(List<byte[]> list, OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        for (byte[] b : list) {
        	InputStream in = new ByteArrayInputStream(b);
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                PdfImportedPage page = writer.getImportedPage(reader, i);
                cb.addTemplate(page, -7, 30);
            }
        }
        outputStream.flush();
        document.close();
        outputStream.close();
    }

    private byte[] generateSchedulePdf(List<ScheduleEvent> scheduleEvents) throws IOException, DocumentException {
	    Document doc = new Document();
	    ByteArrayOutputStream mainBaos = new ByteArrayOutputStream();
	    PdfSmartCopy copy = new PdfSmartCopy(doc, mainBaos);
	    doc.open();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/Prod-Schedule.pdf");
		PdfReader mainReader = new PdfReader(in);
	    for(ScheduleEvent scheduleEvent: scheduleEvents) {
	    	PdfReader reader = new PdfReader(mainReader);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfStamper stamper = new PdfStamper(reader, baos);
	        //---Stamper---
			String scheduleDate = scheduleEvent.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yy"));
			stamper.getAcroFields().setField("scheduleDate", scheduleDate);
			stamper.getAcroFields().setField("line", "LINE: "+String.valueOf(scheduleEvent.getLine().getNumber()));
			String itemNumber = scheduleEvent.getItemPackaging().getItem().getNumber()+" - "+scheduleEvent.getItemPackaging().getItem().getName();
			stamper.getAcroFields().setField("itemNumber", itemNumber);
			stamper.getAcroFields().setField("packagingName", String.valueOf(scheduleEvent.getItemPackaging().getPackaging().getName()));
			String dimensions = scheduleEvent.getItemPackaging().getPackaging().getTi()+" x "+scheduleEvent.getItemPackaging().getPackaging().getHi();
			stamper.getAcroFields().setField("dimensions", dimensions);
			String cartonType = Packaging.TYPE.valueOf(scheduleEvent.getItemPackaging().getPackaging().getType()).label();
			stamper.getAcroFields().setField("cartonType", cartonType);
			SaleItem saleItem = scheduleEvent.getSaleItem();
			String customerSale = "None";
			String cartonLabel = "No";
			String palletTag = "No";
			String priceSticker = "No";
			String expiration = "";
			String unitsSold = "0";
			if(saleItem != null) {
				customerSale = scheduleEvent.getSaleItem().getSale().getCustomer().getName()+" - "+scheduleEvent.getSaleItem().getSale().getNumber();
		        if( scheduleEvent.getSaleItem().getSale().getCustomer().isCartonLabel()) {
		        	cartonLabel =  scheduleEvent.getSaleItem().getSale().getCustomer().getLabelType();
		        }
		        palletTag = scheduleEvent.getSaleItem().getSale().getCustomer().getPalletTagType();
		        priceSticker = scheduleEvent.getSaleItem().getSale().getCustomer().isPriceTicket()?"Yes":"No";
		        if(scheduleEvent.getSaleItem().getExpiration()!=null) {
		        	expiration = scheduleEvent.getSaleItem().getExpiration().format(DateTimeFormatter.ofPattern("MM/dd/yy"));
		        }
		        unitsSold = String.valueOf(scheduleEvent.getSaleItem().getUnits());
			}
			stamper.getAcroFields().setField("customerSale", customerSale);
			stamper.getAcroFields().setField("cartonLabel", cartonLabel);
			stamper.getAcroFields().setField("palletTag", palletTag);
			stamper.getAcroFields().setField("priceSticker", priceSticker);
			stamper.getAcroFields().setField("expiration", expiration);
			stamper.getAcroFields().setField("unitsSold", unitsSold);
			if(scheduleEvent.getItemPackaging().getPackaging().getHologram()!=null) {
				stamper.getAcroFields().setField("hologram", "Hologram: "+scheduleEvent.getItemPackaging().getPackaging().getHologram());	
			}
			long units = scheduleEvent.getUnitsScheduled();
			stamper.getAcroFields().setField("units", String.valueOf(units));
			long cases = (long) Math.ceil((double) units/scheduleEvent.getItemPackaging().getPackaging().getCasePack());
			stamper.getAcroFields().setField("cases", String.valueOf(cases));
			long casesPerPallet = scheduleEvent.getItemPackaging().getPackaging().getHi()*scheduleEvent.getItemPackaging().getPackaging().getTi();
			stamper.getAcroFields().setField("casesPerPallet", String.valueOf(casesPerPallet));
			long pallets = (long) Math.ceil((double) cases/casesPerPallet);
			stamper.getAcroFields().setField("pallets", String.valueOf(pallets));
			stamper.getAcroFields().setField("casePack", String.valueOf(scheduleEvent.getItemPackaging().getPackaging().getCasePack()));
			
			PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages());
			Attachment itemAttachment = scheduleEvent.getItemPackaging().getItem().getAttachment();
			if(itemAttachment != null) {
				Path itemPath = attachmentService.load(itemAttachment.getId());
				if(itemPath != null) {
			        Image itemImage = Image.getInstance(Files.readAllBytes(itemPath));
			        itemImage.setAbsolutePosition(25,568);
			        itemImage.scaleToFit(165, 165);
			        content.addImage(itemImage);
				}
			}
			Attachment packagingAttachment = scheduleEvent.getItemPackaging().getPackaging().getAttachment();
			if(packagingAttachment != null) {
				Path packagingPath = attachmentService.load(packagingAttachment.getId());
				if(packagingPath != null) {
			        Image packagingImage = Image.getInstance(Files.readAllBytes(packagingPath));
			        packagingImage.setAbsolutePosition(300,588);
			        packagingImage.scaleToFit(145,145);
			        content.addImage(packagingImage);
				}
			}
			//---End Stamper---
	        stamper.setFormFlattening(true);
	        stamper.close();
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	    }
	    doc.close();
	    return mainBaos.toByteArray();
	}
	
}