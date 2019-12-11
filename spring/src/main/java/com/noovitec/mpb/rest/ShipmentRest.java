package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.dto.ShipmentDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ShipmentRepo;
import com.noovitec.mpb.service.CrudService;

@RestController
@RequestMapping("/api")
class ShipmentRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentRest.class);
	private ShipmentRepo shipmentRepo;
	@Autowired
	private AttachmentRepo attachmentRepo;
	@Autowired
	private ItemRepo itemRepo;
	@Autowired
	private CrudService crudService;

	public ShipmentRest(ShipmentRepo shipmentRepo) {
		this.shipmentRepo = shipmentRepo;
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
			@RequestParam(name = "number", required = false) String number,
			@RequestParam(name = "customerId", required = false) Long customerId,
			@RequestParam(name = "saleId", required = false) Long saleId,
			@RequestParam(name = "itemId", required = false) Long itemId) {
		List<Long> ids = shipmentRepo.findIds(number, customerId, saleId, itemId);
		if(ids.isEmpty()) {
			return Page.empty();
		}
		Page<Shipment> shipments = shipmentRepo.findPage(pageable, ids);
		Page<ShipmentDto> all = shipments.map(ship -> {
			ShipmentDto dto = new ShipmentDto();
			dto.setId(ship.getId());
			dto.setCustomerId(ship.getCustomer()==null?null:ship.getCustomer().getId());
			dto.setNumber(ship.getNumber());
			dto.setDate(ship.getDate());
			dto.setCustomerName(ship.getCustomer()==null?"":ship.getCustomer().getName());
		    return dto;
		});
		return all;
	}

	@GetMapping("/shipment/{shipmentId}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long shipmentId) throws DocumentException, IOException {
		Shipment shipment = shipmentRepo.findById(shipmentId).get();
		if(shipment.getAttachment()==null) {
			return null;
		}
		byte[] data = shipment.getAttachment().getData();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String fileName = shipment.getAttachment() != null ? shipment.getAttachment().getName() : "BOL" + shipment.getNumber() + "-Draft.pdf";
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
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
		shipment = shipmentRepo.save(shipment);
		//TODO: We might need to keep the audit.
		//TODO: See if it could be refactored after puting merge.
		if(shipment.getAttachment()!=null && shipment.getAttachment().getId()!=null) {
			Long attachmentId = shipment.getAttachment().getId();
			shipment.setAttachment(null);
			attachmentRepo.deleteById(attachmentId);
		}
		byte[] data = this.generatePdf(shipment, true);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "BOL" + shipment.getNumber() + "-" + sdf.format(timestamp) + ".pdf";
		shipment.setAttachment(new Attachment());
		shipment.getAttachment().setData(data);
		shipment.getAttachment().setType("BOL");
		shipment.getAttachment().setName(fileName);
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
			items.add(si.getSaleItem().getItem());
			sales.add(si.getSaleItem().getSale());
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
		String itemQuantity = "";
		String saleNumber = "";
		String itemDescription = "";
		String itemCases = "";
		String itemPallets = "";
		for (ShipmentItem si : shipment.getShipmentItems()) {
			itemQuantity += si.getUnits() + "\n";
			saleNumber += si.getSaleItem().getSale().getNumber() +"\n";
			itemDescription += si.getSaleItem().getItem().getNumber() + " - " +si.getSaleItem().getItem().getName() 
					+ (si.getSaleItem().getSku()==null?"":"SKU# "+ si.getSaleItem().getSku()) + "\n";
			itemCases += si.getCases() + "\n";
			itemPallets += si.getPallets() + "\n";
		}
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/BOL-Template.pdf");
		PdfReader pdfTemplate = new PdfReader(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
		stamper.setFormFlattening(true);
		stamper.getAcroFields().setField("date", shipment.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
		stamper.getAcroFields().setField("number", shipment.getNumber());
		stamper.getAcroFields().setField("via", shipment.getVia());
		stamper.getAcroFields().setField("fob", shipment.getFob());
		stamper.getAcroFields().setField("freightNmfc", shipment.getFreightNmfc());
		stamper.getAcroFields().setField("freightTerms", shipment.getFreightTerms());
		stamper.getAcroFields().setField("loadNumber", shipment.getLoadNumber());
		stamper.getAcroFields().setField("itemQuantity", itemQuantity);
		stamper.getAcroFields().setField("saleNumber", saleNumber);
		stamper.getAcroFields().setField("itemDescription", itemDescription);
		stamper.getAcroFields().setField("itemCases", itemCases);
		stamper.getAcroFields().setField("itemPallets", itemPallets);
		if(shipment.getShippingAddress()!=null) {
			String shippingAddress = shipment.getCustomer().getName() + "\n"
				+ shipment.getShippingAddress().getDc() + "\n"
				+ shipment.getShippingAddress().getStreet() + "\n" 
				+ shipment.getShippingAddress().getCity() + ", " + shipment.getShippingAddress().getState() + " "+shipment.getShippingAddress().getZip() + "\n"
				+ (shipment.getCustomer().getPhone()==null?"":shipment.getCustomer().getPhone());
			stamper.getAcroFields().setField("shippingAddress", shippingAddress);
		}
		if(shipment.getFreightAddress()!=null) {
			String freightAddress = shipment.getFreightAddress().getStreet() + "\n" 
				+ shipment.getFreightAddress().getCity() + ", "+ shipment.getFreightAddress().getState() + " "+shipment.getFreightAddress().getZip();		
			stamper.getAcroFields().setField("freightAddress", freightAddress);
		}
		stamper.getAcroFields().setField("notes", shipment.getNotes());
		stamper.getAcroFields().setField("totalUnits", shipment.getTotalUnits().toString());
		stamper.getAcroFields().setField("totalCases", shipment.getTotalCases().toString());
		stamper.getAcroFields().setField("totalPallets", shipment.getTotalPallets().toString());
		stamper.getAcroFields().setField("totalWeight", shipment.getTotalWeight().toString());
//		if (!submitted) {
//			PdfContentByte under = stamper.getUnderContent(1);
//			PdfGState gs1 = new PdfGState();
//			gs1.setFillOpacity(0.5f);
//			under.setGState(gs1);
//			Font f = new Font(FontFamily.HELVETICA, 15);
//			Phrase p = new Phrase("DAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT", f);
//			ColumnText.showTextAligned(under, Element.ALIGN_CENTER, p, 300, 400, 45f);
//		}
		stamper.close();
		pdfTemplate.close();
		return baos.toByteArray();
	}

}