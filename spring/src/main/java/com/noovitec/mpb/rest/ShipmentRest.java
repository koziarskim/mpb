package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ShipmentRepo;

@RestController
@RequestMapping("/api")
class ShipmentRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentRest.class);
	private ShipmentRepo shipmentRepo;
	@Autowired
	private AttachmentRepo attachmentRepo;

	public ShipmentRest(ShipmentRepo shipmentRepo) {
		this.shipmentRepo = shipmentRepo;
	}

	@GetMapping("/shipment")
	Collection<Shipment> getAll() {
		return shipmentRepo.findAll();
	}

	@GetMapping("/shipment/{id}")
	ResponseEntity<Shipment> get(@PathVariable Long id) {
		Optional<Shipment> result = shipmentRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/shipment/{id}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long id) throws DocumentException, IOException {
		Shipment shipment = shipmentRepo.findById(id).get();
		byte[] data = null;
		if (shipment.isSubmitted() && shipment.getAttachment() != null) {
			Attachment attachment = attachmentRepo.findById(shipment.getAttachment().getId()).get();
			data = attachment.getData();
		} else {
			data = this.generatePdf(shipment, false);
		}
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String fileName = shipment.getAttachment() != null ? shipment.getAttachment().getName() : "PO" + shipment.getNumber() + "-Draft.pdf";
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@PostMapping("/shipment")
	ResponseEntity<Shipment> post(@RequestBody(required = false) Shipment shipment) throws URISyntaxException {
		if (shipment == null) {
			shipment = new Shipment();
		}
		for(ShipmentItem si: shipment.getShipmentItems()) {
			si.setShipment(shipment);
		}
		Shipment result = shipmentRepo.save(shipment);
		if(result.getNumber()==null) {
			String number = result.getId().toString();
			if(number.length()==1) {
				number = "00"+number;
			}
			if(number.length() == 2) {
				number = "0"+number;
			}
			result.setNumber(number);
			shipmentRepo.save(result);			
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/shipment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		shipmentRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
	private byte[] generatePdf(Shipment shipment, boolean submitted) throws IOException, DocumentException {
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		String componentName = "";
		String componentDescription = "";
		String componentUnits = "";
		String componentPrice = "";
		String componentTotalPrice = "";
		List<ComponentDto> dtos = new ArrayList<ComponentDto>();
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
//		stamper.getAcroFields().setField("number", purchase.getNumber());
//		stamper.getAcroFields().setField("supplierName", purchase.getSupplier().getName());
//		stamper.getAcroFields().setField("paymentTerms", purchase.getSupplier().getPaymentTerms());
//		stamper.getAcroFields().setField("expectedDate", purchase.getExpectedDate() != null ? dateFormat.format(purchase.getExpectedDate()) : "");
//		stamper.getAcroFields().setField("freighTerms", purchase.getSupplier().getFreightTerms());
		stamper.getAcroFields().setField("componentName", componentName);
		stamper.getAcroFields().setField("componentDescription", componentDescription);
		stamper.getAcroFields().setField("componentUnits", componentUnits);
		stamper.getAcroFields().setField("componentPrice", componentPrice);
		stamper.getAcroFields().setField("componentTotalPrice", componentTotalPrice);
//		stamper.getAcroFields().setField("totalPrice", currencyFormat.format(purchase.getTotalPrice()));
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