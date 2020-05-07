package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.PurchaseListDto;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.ItemComponent;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Supplier;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.SupplierRepo;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.PurchaseService;


@RestController
@RequestMapping("/api")
class PurchaseRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;

	private final Logger log = LoggerFactory.getLogger(PurchaseRest.class);
	private PurchaseService purchaseService;
	
	@Autowired
	private ComponentRepo componentRepo;
	@Autowired
	private SupplierRepo supplierRepo;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private PurchaseRepo purchaseRepo;
	
	public PurchaseRest(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	@GetMapping("/purchase")
	List<Purchase> getAll() {
		return purchaseRepo.findAll();
	}

	@GetMapping("/purchase/kv")
	List<KeyValueDto> getAllKv(@RequestParam(name = "component_id", required = false) Long component_id) {
		List<KeyValueDto> dtos = null;
		if(component_id ==null) {
			dtos = purchaseRepo.findAllKv();
		}else {
			dtos = purchaseRepo.findKvByComponnet(component_id);
		}
		return dtos;
	}
	
	@GetMapping("/purchase/number/{date}")
	ResponseEntity<String> getAvailableNumber(@PathVariable(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		Long result = purchaseRepo.getLastNumber(date);
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

	@GetMapping("/purchase/pageable")
	Page<PurchaseListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, 
			@RequestParam(required = false) String purchaseName, 
			@RequestParam(required = false) Long componentId,
			@RequestParam(required = false) Long supplierId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String freightTerms) {
		Page<Purchase> purchases = purchaseRepo.findPagable(pageable, purchaseName, componentId, supplierId, status, freightTerms);
		Page<PurchaseListDto> dtos = purchases.map(purchase -> {
			PurchaseListDto dto = new PurchaseListDto();
			dto.setId(purchase.getId());
			dto.setEtaDate(purchase.getExpectedDate());
			dto.setInvoiceNumber(purchase.getInvoiceNumber());
			dto.setName(purchase.getName());
			dto.setNumber(purchase.getNumber());
			dto.setPoDate(purchase.getDate());
			dto.setShippingDate(purchase.getShippingDate());
			dto.setSupplierName(purchase.getSupplier().getName());
			dto.setFreightTermId(purchase.getSupplier().getFreightTerms());
			dto.setUnitsPurchased(purchase.getUnitsPurchased());
			dto.setUnitsReceived(purchase.getUnitsReceived());
			return dto;
		});
		return dtos;
	}

	@GetMapping("/purchase/{id}")
	ResponseEntity<Purchase> get(@PathVariable Long id) {
		Optional<Purchase> result = purchaseRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/purchase/{id}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long id) throws DocumentException, IOException {
		Purchase purchase = purchaseRepo.findById(id).get();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "PO_"+purchase.getNumber()+"_"+purchase.getId() + "-" + sdf.format(timestamp) +".pdf";
		byte[] data = this.generatePdf(purchase);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/purchase/validate/number/{number}")
	ResponseEntity<?> validateNumber(@PathVariable String number) {
		Long id = purchaseRepo.getIdByNumber(number);
		if(id==null) {
			return ResponseEntity.ok().build();
		}else {
			return ResponseEntity.ok().body("Duplicate");
		}
	}

	// Save and update.
	@PostMapping("/purchase")
	ResponseEntity<?> post(@RequestBody Purchase purchase) throws IOException, DocumentException{
		if(!purchase.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = purchaseRepo.getIdByNumber(purchase.getNumber());
		if((purchase.getId()==null && id !=null) || (purchase.getId()!=null && id !=null && !purchase.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Purchase Number already exists. Please, choose differrent.");
		}

		purchase = purchaseRepo.save(purchase);
		List<Long> componentIds = new ArrayList<Long>();
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			pc.setPurchase(purchase);
			componentIds.add(pc.getComponent().getId());
		}
		purchase = purchaseRepo.save(purchase);
		purchaseService.updateUnits(purchase.getId());
		List<Long> itemIds = itemService.findIdsByComponents(componentIds);
		itemService.updateUnits(itemIds);
		componentService.updateUnits(componentIds);
		itemService.updateUnitsReadyProd(itemIds);
		return ResponseEntity.ok().body(purchase);
	}

	@DeleteMapping("/purchase/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Purchase purchase = purchaseRepo.getOne(id);
		if(purchase.getPurchaseComponents().size()>0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Components!");
		}
		List<Long> itemIds = new ArrayList<Long>();
		List<Long> componentIds = new ArrayList<Long>();
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			pc.setPurchase(purchase);
			componentIds.add(pc.getComponent().getId());
			for(ItemComponent ic: pc.getComponent().getItemComponents()) {
				itemIds.add(ic.getItem().getId());
			}
		}
		purchaseRepo.deleteById(id);
		if(itemIds.size()>0) {
			itemService.updateUnits(itemIds);
		}
		if(componentIds.size()>0) {
			componentService.updateUnits(componentIds);
		}
		if(itemIds.size()>0) {
			itemService.updateUnitsReadyProd(itemIds);
		}
		return ResponseEntity.ok().build();
	}

	private byte[] generatePdf(Purchase purchase) throws IOException, DocumentException {
		Supplier s = supplierRepo.findById(purchase.getSupplier().getId()).get();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		currencyFormat.setMaximumFractionDigits(4);
		String componentName = "";
		String componentDescription = "";
		String componentUnits = "";
		String componentPrice = "";
		String componentTotalPrice = "";
		String componentCases = "";
		BigDecimal totalCases = BigDecimal.ZERO;
		long totalUnits = 0;
		
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			Component c = this.componentRepo.findById(pc.getComponent().getId()).get();
			componentName += c.getNumber() + "\n";
			componentDescription += c.getName() +", "+ (c.getSupplierStockNumber()==null?"":"Item: "+c.getSupplierStockNumber()) + "\n";
			componentUnits += pc.getUnits() + "\n";
			componentPrice += currencyFormat.format(pc.getUnitPrice()) + "\n";
			componentTotalPrice += currencyFormat.format(pc.getTotalPrice()) + "\n";
			BigDecimal cases = BigDecimal.valueOf(pc.getUnits()/pc.getComponent().getCasePack()).setScale(0, RoundingMode.CEILING);
			componentCases += cases+ "\n";
			totalCases = totalCases.add(cases);
			totalUnits += pc.getUnits();
		}
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/PO-Template.pdf");
		PdfReader pdfTemplate = new PdfReader(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
		stamper.setFormFlattening(true);
		stamper.getAcroFields().setField("date", purchase.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
		stamper.getAcroFields().setField("number", purchase.getNumber());
		String supplierInfo = s.getName() + "\n"
				+(s.getStreet()==null?"":(s.getStreet()+"\n"))
				+(s.getCity()==null?"":(s.getCity()+", "))+(s.getState()==null?"":(s.getState()+" "))+(s.getZip()==null?"":s.getZip());
		stamper.getAcroFields().setField("supplierName", supplierInfo);
		stamper.getAcroFields().setField("paymentTerms", s.getPaymentTerms());
		stamper.getAcroFields().setField("expectedDate", purchase.getExpectedDate() != null ? purchase.getExpectedDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")) : "");
		stamper.getAcroFields().setField("freighTerms", s.getFreightTerms());
		stamper.getAcroFields().setField("componentName", componentName);
		stamper.getAcroFields().setField("componentDescription", componentDescription);
		stamper.getAcroFields().setField("componentUnits", componentUnits);
		stamper.getAcroFields().setField("componentPrice", componentPrice);
		stamper.getAcroFields().setField("componentCases", componentCases);
		stamper.getAcroFields().setField("componentTotalPrice", componentTotalPrice);
		stamper.getAcroFields().setField("totalPrice", currencyFormat.format(purchase.getTotalPrice()));
		stamper.getAcroFields().setField("totalCases", totalCases.toString());
		stamper.getAcroFields().setField("totalUnits", String.valueOf(totalUnits));
		stamper.close();
		pdfTemplate.close();
		return baos.toByteArray();
	}
}