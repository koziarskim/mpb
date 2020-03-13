package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.ShipmentDto;
import com.noovitec.mpb.dto.ShipmentEventDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.ShipmentRepo;
import com.noovitec.mpb.service.AttachmentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.ShipmentService;

@RestController
@RequestMapping("/api")
class ShipmentRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentRest.class);
	private ShipmentService shipmentService;
	@Autowired
	private ShipmentRepo shipmentRepo;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private CrudService crudService;
	
	
	public ShipmentRest(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}

	@GetMapping("/shipment/{id}")
	ResponseEntity<Shipment> get(@PathVariable Long id) {
		Optional<Shipment> result = shipmentRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/shipment/number/{date}")
	ResponseEntity<String> getAvailableNumber(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		Long result = shipmentRepo.getLastNumber(date);
		String number = String.valueOf(result+1);
		if(number.length()==1) {
			number = "00"+number;
		} else if(number.length()==2) {
			number = "0"+number;
		}
		String month = date.getMonthValue()<10?"0"+String.valueOf(date.getMonthValue()):String.valueOf(date.getMonthValue());
		String day = date.getDayOfMonth()<10?"0"+String.valueOf(date.getDayOfMonth()):String.valueOf(date.getDayOfMonth());
		return ResponseEntity.ok().body(String.valueOf(date.getYear())+month+day+number);
	}
	
	@GetMapping("/shipment/pageable")
	Page<ShipmentDto> getAllPageable(
			@RequestParam Pageable pageable, 
			@RequestParam(required = false) String number,
			@RequestParam(required = false) Long customerId,
			@RequestParam(required = false) Long saleId,
			@RequestParam(required = false) Long itemId, 
			@RequestParam(required = false) String status,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipTo) {
		Page<Shipment> shipments = shipmentRepo.findIds(pageable, number, customerId, saleId, itemId, status, shipFrom, shipTo);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter windowFormat = DateTimeFormatter.ofPattern("MM/dd");
		Page<ShipmentDto> all = shipments.map(ship -> {
			ShipmentDto dto = new ShipmentDto();
			dto.setId(ship.getId());
			dto.setCustomerId(ship.getCustomer()==null?null:ship.getCustomer().getId());
			dto.setNumber(ship.getNumber());
			dto.setShippingDate(ship.getShippingDate());
			dto.setShippedDate(ship.getShippedDate());
			dto.setModifiedDate(ship.getModifiedDate().format(dateFormat));
			String shippingFrom = ship.getShippingFrom()==null?"":ship.getShippingFrom().format(windowFormat);
			String shippingTo = ship.getShippingTo()==null?"":ship.getShippingTo().format(windowFormat);
			dto.setShippingWindow(shippingFrom +" - "+shippingTo);
			dto.setCustomerName(ship.getCustomer()==null?"":ship.getCustomer().getName());
			dto.setReady(ship.isReady());
			dto.setStatus(ship.getStatus());
		    return dto;
		});
		return all;
	}

	@GetMapping("/shipment/{shipmentId}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long shipmentId) throws DocumentException, IOException {
		Shipment shipment = shipmentRepo.findById(shipmentId).get();
		Attachment attachment = attachmentService.getById(shipment.getAttachment().getId());
		if(attachment == null) {
			attachment = new Attachment();
			attachment.setType("SHIPMENT");
			attachment.setMimeType("PDF");
			attachment.setName("BOL_"+shipment.getNumber()+"_"+shipment.getId());
		}
		byte[] data = this.generatePdf(shipment, true);
		attachmentService.save(attachment, data);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + attachment.getFileName());
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/shipment/events/ready")
	HttpEntity<List<ShipmentEventDto>> getEvents() {
		List<ShipmentEventDto> shipments = shipmentService.findEvents();
		return ResponseEntity.ok().body(shipments);
	}	
	
	@PostMapping("/shipment")
	ResponseEntity<Shipment> post(@RequestBody(required = false) Shipment shipment) throws Exception {
		if (shipment == null) {
			shipment = new Shipment();
		}
		for(ShipmentItem si: shipment.getShipmentItems()) {
			si.setShipment(shipment);
		}
		shipment = (Shipment) crudService.merge(shipment);
		for(ShipmentItem si: shipment.getShipmentItems()) {
			si.getSaleItem().getItem().updateUnits();
			si.getSaleItem().getSale().updateUnits();
		}
		//Set modifiedDate
		shipment.setModifiedDate(LocalDateTime.now());
		//Set Status
		String status = "INP";
		if(shipment.isReady()) {
			status = "REA";
		}
		if(shipment.getShippedDate()!=null) {
			status = "SHP";
		}
		shipment.setStatus(status);
		shipment = shipmentRepo.save(shipment);
		byte[] data = this.generatePdf(shipment, true);
		Attachment attachment = shipment.getAttachment();
		if(attachment == null) {
			attachment = new Attachment();
			attachment.setType("SHP");
			attachment.setMimeType("PDF");
			attachment.setName("BOL_" + shipment.getNumber() + "_" + shipment.getId());
			attachment = attachmentService.save(attachment, data);
			shipment.setAttachment(attachment);
		}
		shipment = shipmentRepo.save(shipment);
		return ResponseEntity.ok().body(shipment);
	}

	@DeleteMapping("/shipment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		//TODO: Is there a better way of doing it?
		List<Item> items = new ArrayList<Item>();
		List<Sale> sales = new ArrayList<Sale>();
		Shipment shipment = shipmentRepo.findById(id).get();
		for(ShipmentItem si: shipment.getShipmentItems()) {
			if(si.getSaleItem()!=null) {
				items.add(si.getSaleItem().getItem());
				sales.add(si.getSaleItem().getSale());
			}
		}
		shipmentRepo.deleteById(id);
		for(Item item: items) {
			item.updateUnits();
			crudService.save(item);
		}
		for(Sale sale: sales) {
			sale.updateUnits();
			crudService.save(sale);
		}
		return ResponseEntity.ok().build();
	}
	
	private byte[] generatePdf(Shipment shipment, boolean submitted) throws IOException, DocumentException {
		Map<String, String> ft = new HashMap<String, String>();
		ft.put("TPB", "TP Bill");
		ft.put("PRP", "Pre Paid");
		ft.put("TPO", "TP Bill Other");
		ft.put("COL","Collect");
		String itemQuantity = "";
		String saleNumber = "";
		String itemDescription = "";
		String itemCasePack = "";
		String itemCases = "";
		String itemPallets = "";
		String itemQuantity2 = "";
		String saleNumber2 = "";
		String itemDescription2 = "";
		String itemCasePack2 = "";
		String itemCases2 = "";
		String itemPallets2 = "";
		int totalCasePack = 0;
		Collection<ShipmentItem> shipmentItems = shipment.getShipmentItems();
		int count = 0;
		for (ShipmentItem si : shipmentItems) {
			count++;
			if(count <= 30) {
				saleNumber += si.getSaleItem().getSale().getNumber() +"\n";
				itemQuantity += si.getUnits() + "\n";
				itemDescription += si.getSaleItem().getItem().getNumber() + " - " +si.getSaleItem().getItem().getName() 
						+ (si.getSaleItem().getSku()==null?"":" SKU# "+ si.getSaleItem().getSku()) + "\n";
				itemCasePack += si.getSaleItem().getItem().getCasePack() + "\n";
				itemCases += si.getCases() + "\n";
				itemPallets += si.getPallets()==null?"":si.getPallets() + "\n";
			}else {
				saleNumber2 += si.getSaleItem().getSale().getNumber() +"\n";
				itemQuantity2 += si.getUnits() + "\n";
				itemDescription2 += si.getSaleItem().getItem().getNumber() + " - " +si.getSaleItem().getItem().getName() 
						+ (si.getSaleItem().getSku()==null?"":" SKU# "+ si.getSaleItem().getSku()) + "\n";
				itemCasePack2 += si.getSaleItem().getItem().getCasePack() + "\n";
				itemCases2 += si.getCases() + "\n";
				itemPallets2 += si.getPallets()==null?"":si.getPallets() + "\n";
			}
			totalCasePack += si.getSaleItem().getItem().getCasePack();
		}
		InputStream bolIn = null;
		if(shipmentItems.size() <= 30) {
			bolIn = this.getClass().getClassLoader().getResourceAsStream("pdf/BOL-Template-1.pdf");
		}else {
			bolIn = this.getClass().getClassLoader().getResourceAsStream("pdf/BOL-Template-2.pdf");
		}
		PdfReader bolTemplate = new PdfReader(bolIn);
		ByteArrayOutputStream bolBaos = new ByteArrayOutputStream();
		PdfStamper bolStamper = new PdfStamper(bolTemplate, bolBaos);
		bolStamper.setFormFlattening(true);
		bolStamper.getAcroFields().setField("date", (shipment.getModifiedDate()==null?"":shipment.getModifiedDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy"))));
		bolStamper.getAcroFields().setField("shippingDate", (shipment.getShippingDate()==null?"":shipment.getShippingDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy"))));
		bolStamper.getAcroFields().setField("number", shipment.getNumber());
		bolStamper.getAcroFields().setField("via", shipment.getVia());
		bolStamper.getAcroFields().setField("fob", shipment.getFob());
		bolStamper.getAcroFields().setField("freightNmfc", shipment.getFreightNmfc());
		bolStamper.getAcroFields().setField("freightClass", shipment.getFreightClass());
		bolStamper.getAcroFields().setField("freightTerms",  ft.get(shipment.getFreightTerms()));
		bolStamper.getAcroFields().setField("loadNumber", shipment.getLoadNumber());
		bolStamper.getAcroFields().setField("saleNumber", saleNumber);
		bolStamper.getAcroFields().setField("itemQuantity", itemQuantity);
		bolStamper.getAcroFields().setField("itemDescription", itemDescription);
		bolStamper.getAcroFields().setField("itemCasePack", itemCasePack);
		bolStamper.getAcroFields().setField("itemCases", itemCases);
		bolStamper.getAcroFields().setField("itemPallets", itemPallets);
		bolStamper.getAcroFields().setField("saleNumber2", saleNumber2);
		bolStamper.getAcroFields().setField("itemQuantity2", itemQuantity2);
		bolStamper.getAcroFields().setField("itemDescription2", itemDescription2);
		bolStamper.getAcroFields().setField("itemCasePack2", itemCasePack2);
		bolStamper.getAcroFields().setField("itemCases2", itemCases2);
		bolStamper.getAcroFields().setField("itemPallets2", itemPallets2);
		if(shipment.getShippingAddress()!=null) {
			String phone = shipment.getShippingAddress().getPhone()==null?shipment.getCustomer().getPhone():shipment.getShippingAddress().getPhone();
			String shippingAddress = shipment.getCustomer().getName() + " - "+shipment.getShippingAddress().getDc() + "\n"
				+ (shipment.getShippingAddress().getLine()==null?"":(shipment.getShippingAddress().getLine() + "\n"))	
				+ shipment.getShippingAddress().getStreet() + "\n" 
				+ shipment.getShippingAddress().getCity() + ", " + shipment.getShippingAddress().getState() + " "+shipment.getShippingAddress().getZip() + "\n"
				+ (phone==null?"":("Phone: "+phone + "\n"))
				+ (shipment.getShippingAddress().getNotes()==null?"":shipment.getShippingAddress().getNotes());
			bolStamper.getAcroFields().setField("shippingAddress", shippingAddress);
		}
		if(shipment.getFreightAddress()!=null) {
			String freightAddress = shipment.getFreightAddress().getDc() + "\n"
				+ (shipment.getFreightAddress().getLine()==null?"":(shipment.getFreightAddress().getLine() + "\n"))
				+ shipment.getFreightAddress().getStreet() + "\n" 
				+ shipment.getFreightAddress().getCity() + ", "+ shipment.getFreightAddress().getState() + " "+shipment.getFreightAddress().getZip() + "\n"		
				+ (shipment.getFreightAddress().getPhone()==null?"":("Phone: "+shipment.getFreightAddress().getPhone() + "\n"))
				+ (shipment.getFreightAddress().getNotes()==null?"":shipment.getFreightAddress().getNotes());
			bolStamper.getAcroFields().setField("freightAddress", freightAddress);
		}
		bolStamper.getAcroFields().setField("notes", shipment.getNotes());
		bolStamper.getAcroFields().setField("totalUnits", shipment.getTotalUnits().toString());
		bolStamper.getAcroFields().setField("totalCasePack", String.valueOf(totalCasePack));
		bolStamper.getAcroFields().setField("totalCases", shipment.getTotalCases().toString());
		bolStamper.getAcroFields().setField("totalPallets", shipment.getTotalPalletsCustom().toString());
		bolStamper.getAcroFields().setField("totalWeight", shipment.getTotalWeightCustom().toString());
		bolStamper.close();
		bolTemplate.close();
		
		return bolBaos.toByteArray();
	}

}