package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.PurchaseSale;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.PurchaseRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class PurchaseRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;

	private final Logger log = LoggerFactory.getLogger(PurchaseRest.class);
	private PurchaseRepo purchaseRepo;

	public PurchaseRest(PurchaseRepo purchaseRepo) {
		this.purchaseRepo = purchaseRepo;
	}

	@GetMapping("/purchase")
	Collection<Purchase> getAll() {
		return purchaseRepo.findAll();
	}

	@GetMapping("/purchase/{id}")
	ResponseEntity<Purchase> get(@PathVariable Long id) {
		Optional<Purchase> result = purchaseRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/purchase/{id}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long id) throws DocumentException, IOException {
		Purchase purchase = purchaseRepo.findById(id).get();
		if (purchase.getAttachment() == null || !purchase.isLocked()) {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/PO-Template.pdf");
			PdfReader pdfTemplate = new PdfReader(in);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
			stamper.setFormFlattening(true);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyy");
			stamper.getAcroFields().setField("date", dateFormatter.format(new Date()));
			stamper.getAcroFields().setField("number", purchase.getNumber());
			stamper.getAcroFields().setField("supplierName", purchase.getSupplier().getName());
			stamper.getAcroFields().setField("paymentTerms", purchase.getSupplier().getPaymentTerms());
			stamper.getAcroFields().setField("expectedDate", purchase.getExpectedDate()!=null?dateFormatter.format(purchase.getExpectedDate()):"");
			stamper.getAcroFields().setField("freighTerms", purchase.getSupplier().getFreightTerms());
			stamper.getAcroFields().setField("componentName", "???");
			stamper.getAcroFields().setField("componentDescription", "???");
			stamper.getAcroFields().setField("componentUnits", "???");
			stamper.getAcroFields().setField("componentPrice", "???");
			stamper.getAcroFields().setField("componentTotalPrice", "???");
			stamper.getAcroFields().setField("totalPrice", NumberFormat.getCurrencyInstance().format(purchase.getTotalPrice()));
			stamper.close();
			pdfTemplate.close();
			byte[] data = baos.toByteArray();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = "PO" + purchase.getNumber() + "-" + sdf.format(timestamp) + ".pdf";
			Attachment attachment = new Attachment();
			attachment.setData(data);
			attachment.setType("POR");
			attachment.setName(fileName);
			purchase.setAttachment(attachment);
			purchaseRepo.save(purchase);
		}
		Attachment attachment = attachmentRepo.findById(purchase.getAttachment().getId()).get();
		byte[] data = attachment.getData();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + attachment.getName());
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@PostMapping("/purchase")
	ResponseEntity<Purchase> post(@RequestBody(required = false) Purchase purchase)
			throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		if (purchase == null) {
			purchase = new Purchase();
		}
		for (PurchaseSale ps : purchase.getPurchaseSales()) {
			ps.setPurchase(purchase);
		}
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			pc.setPurchase(purchase);
		}
		Purchase result = purchaseRepo.save(purchase);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/purchase/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
//		Optional<Sale> sale = saleRepo.findById(id);
//		sale.get().getCustomer().getAddresses().remove(sale.get());
//		saleRepo.delete(sale.get());
		purchaseRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}