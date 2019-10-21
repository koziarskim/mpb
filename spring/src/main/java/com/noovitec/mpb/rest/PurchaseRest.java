package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.Supplier;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;
import com.noovitec.mpb.repo.SupplierRepo;


@RestController
@RequestMapping("/api")
class PurchaseRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;

	private final Logger log = LoggerFactory.getLogger(PurchaseRest.class);
	private PurchaseRepo purchaseRepo;
	
	@Autowired
	ComponentRepo componentRepo;
	@Autowired
	SupplierRepo supplierRepo;

	public PurchaseRest(PurchaseRepo purchaseRepo) {
		this.purchaseRepo = purchaseRepo;
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

	@GetMapping("/purchase/pageable")
	Page<Purchase> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, 
			@RequestParam(name = "searchKey", required = false) String searchKey, @RequestParam(name = "searchType", required = false) String searchType) {
		Page<Purchase> purchases = null;
		if(searchType==null || searchType.isBlank() || searchKey==null || searchKey.isBlank()) {
			purchases = purchaseRepo.findPage(pageable);
		}else if(searchType.equals("purchase") && !searchKey.isBlank()) {
			purchases = purchaseRepo.findPageByPurchase(pageable, searchKey);
		}else if(searchType.equals("component") && !searchKey.isBlank()){
			purchases = purchaseRepo.findPageByComponent(pageable, searchKey);
		}
		if(purchases == null) {
			 purchases = Page.empty();
		}
		return purchases;
	}

	@GetMapping("/purchase/active")
	Collection<Purchase> getAllActive(@RequestParam(name = "component_id", required = false) Long component_id) {
		Collection<Purchase> result = new HashSet<Purchase>();
		for(Purchase p : (component_id == null?purchaseRepo.findAll():purchaseRepo.findByComponent(component_id))) {
			if(!p.isReceived()) {
				result.add(p);
			}
		}
		return result;
	}

	//TODO: Use getAll instead.
	@GetMapping("/purchase/submitted")
	Collection<Purchase> getAllSubmitted() {
		Collection<Purchase> result = purchaseRepo.findAll();
		return result;
	}

	@GetMapping("/purchase/{id}")
	ResponseEntity<Purchase> get(@PathVariable Long id) {
		Optional<Purchase> result = purchaseRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/purchase/{id}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long id) throws DocumentException, IOException {
		Purchase purchase = purchaseRepo.findById(id).get();
		byte[] data = null;
		Attachment attachment = attachmentRepo.findById(purchase.getAttachment().getId()).get();
		data = attachment.getData();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String fileName = attachment.getName();
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	// Save and update.
	@PostMapping("/purchase")
	ResponseEntity<Purchase> post(@RequestBody(required = false) Purchase purchase)
			throws URISyntaxException, JsonParseException, JsonMappingException, IOException, DocumentException {
		if (purchase == null) {
			purchase = new Purchase();
		}
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			pc.setPurchase(purchase);
		}
		Purchase result = purchaseRepo.save(purchase);
		byte[] data = this.generatePdf(result);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "PO" + purchase.getNumber() + "-" + sdf.format(timestamp) + ".pdf";
		Attachment attachment = new Attachment();
		attachment.setData(data);
		attachment.setType("POR");
		attachment.setName(fileName);
		purchase.setAttachment(attachment);
		result = purchaseRepo.save(purchase);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/purchase/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		purchaseRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

	private byte[] generatePdf(Purchase purchase) throws IOException, DocumentException {
		Supplier s = supplierRepo.findById(purchase.getSupplier().getId()).get();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		String componentName = "";
		String componentDescription = "";
		String componentUnits = "";
		String componentPrice = "";
		String componentTotalPrice = "";
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			Component c = this.componentRepo.findById(pc.getComponent().getId()).get();
			componentName += c.getNumber() + "\n";
			componentDescription += c.getName() + "\n";
			componentUnits += pc.getUnits() + "\n";
			componentPrice += currencyFormat.format(pc.getUnitPrice()) + "\n";
			componentTotalPrice += currencyFormat.format(pc.getTotalPrice()) + "\n";
		}
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/PO-Template.pdf");
		PdfReader pdfTemplate = new PdfReader(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
		stamper.setFormFlattening(true);
		stamper.getAcroFields().setField("date", purchase.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
		stamper.getAcroFields().setField("number", purchase.getNumber());
		stamper.getAcroFields().setField("supplierName", s.getName());
		stamper.getAcroFields().setField("paymentTerms", s.getPaymentTerms());
		stamper.getAcroFields().setField("expectedDate", purchase.getExpectedDate() != null ? purchase.getExpectedDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")) : "");
		stamper.getAcroFields().setField("freighTerms", s.getFreightTerms());
		stamper.getAcroFields().setField("componentName", componentName);
		stamper.getAcroFields().setField("componentDescription", componentDescription);
		stamper.getAcroFields().setField("componentUnits", componentUnits);
		stamper.getAcroFields().setField("componentPrice", componentPrice);
		stamper.getAcroFields().setField("componentTotalPrice", componentTotalPrice);
		stamper.getAcroFields().setField("totalPrice", currencyFormat.format(purchase.getTotalPrice()));
		stamper.close();
		pdfTemplate.close();
		return baos.toByteArray();
	}
}