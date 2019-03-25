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
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.ComponentDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Purchase;
import com.noovitec.mpb.entity.PurchaseComponent;
import com.noovitec.mpb.entity.PurchaseSale;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
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
	@Autowired
	private ComponentRepo componentRepo;

	public PurchaseRest(PurchaseRepo purchaseRepo) {
		this.purchaseRepo = purchaseRepo;
	}

	@GetMapping("/purchase")
	Collection<Purchase> getAll(@RequestParam(name = "component_id", required = false) Long component_id) {
		Collection<Purchase> result = null;
		if (component_id != null) {
			result = purchaseRepo.findByComponent(component_id);
		} else {
			result = purchaseRepo.findAll();
		}
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
		if (purchase.isSubmitted() && purchase.getAttachment() != null) {
			Attachment attachment = attachmentRepo.findById(purchase.getAttachment().getId()).get();
			data = attachment.getData();
		} else {
			data = this.generatePdf(purchase, false);
		}
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String fileName = purchase.getAttachment() != null ? purchase.getAttachment().getName() : "PO" + purchase.getNumber() + "-Draft.pdf";
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
		for (PurchaseSale ps : purchase.getPurchaseSales()) {
			ps.setPurchase(purchase);
		}
		for (PurchaseComponent pc : purchase.getPurchaseComponents()) {
			pc.setPurchase(purchase);
		}
		if (purchase.isSubmitted() && purchase.getAttachment() == null) {
			byte[] data = this.generatePdf(purchase, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = "PO" + purchase.getNumber() + "-" + sdf.format(timestamp) + ".pdf";
			Attachment attachment = new Attachment();
			attachment.setData(data);
			attachment.setType("POR");
			attachment.setName(fileName);
			purchase.setAttachment(attachment);
		}
		Purchase result = purchaseRepo.save(purchase);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/purchase/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		purchaseRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

	private byte[] generatePdf(Purchase purchase, boolean submitted) throws IOException, DocumentException {
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		String componentName = "";
		String componentDescription = "";
		String componentUnits = "";
		String componentPrice = "";
		String componentTotalPrice = "";
		List<ComponentDto> dtos = componentRepo.getComponentsForPurchaseAndSupplier(purchase.getId(), purchase.getSupplier().getId());
		for (ComponentDto dto : dtos) {
			if (dto.isSelected()) {
				componentName += dto.getNumber() + "\n";
				componentDescription += dto.getName() + "\n";
				componentUnits += dto.getUnits() + "\n";
				componentPrice += currencyFormat.format(dto.getUnitPrice()) + "\n";
				componentTotalPrice += currencyFormat.format(dto.getTotalPrice()) + "\n";
			}
		}
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/PO-Template.pdf");
		PdfReader pdfTemplate = new PdfReader(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
		stamper.setFormFlattening(true);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
		stamper.getAcroFields().setField("date", dateFormat.format(new Date()));
		stamper.getAcroFields().setField("number", purchase.getNumber());
		stamper.getAcroFields().setField("supplierName", purchase.getSupplier().getName());
		stamper.getAcroFields().setField("paymentTerms", purchase.getSupplier().getPaymentTerms());
		stamper.getAcroFields().setField("expectedDate", purchase.getExpectedDate() != null ? dateFormat.format(purchase.getExpectedDate()) : "");
		stamper.getAcroFields().setField("freighTerms", purchase.getSupplier().getFreightTerms());
		stamper.getAcroFields().setField("componentName", componentName);
		stamper.getAcroFields().setField("componentDescription", componentDescription);
		stamper.getAcroFields().setField("componentUnits", componentUnits);
		stamper.getAcroFields().setField("componentPrice", componentPrice);
		stamper.getAcroFields().setField("componentTotalPrice", componentTotalPrice);
		stamper.getAcroFields().setField("totalPrice", currencyFormat.format(purchase.getTotalPrice()));
		if (!submitted) {
			PdfContentByte under = stamper.getUnderContent(1);
			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(0.5f);
			under.setGState(gs1);
			Font f = new Font(FontFamily.HELVETICA, 15);
			Phrase p = new Phrase("DAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT...DRAFT", f);
			ColumnText.showTextAligned(under, Element.ALIGN_CENTER, p, 300, 400, 45f);
		}
		stamper.close();
		pdfTemplate.close();
		return baos.toByteArray();
	}
}