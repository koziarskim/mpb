package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
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
import org.springframework.web.client.HttpServerErrorException;

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
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.ShipmentRepo;

@RestController
@RequestMapping("/api")
class ShipmentRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentRest.class);
	private ShipmentRepo shipmentRepo;
	@Autowired
	private AttachmentRepo attachmentRepo;
	@Autowired
	private ItemRepo itemRepo;

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
		if (result.isSubmitted() && result.getAttachment() == null) {
			byte[] data = this.generatePdf(result, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = "BOL" + result.getNumber() + "-" + sdf.format(timestamp) + ".pdf";
			Attachment attachment = new Attachment();
			attachment.setData(data);
			attachment.setType("BOL");
			attachment.setName(fileName);
			result.setAttachment(attachment);
		}
		shipmentRepo.save(result);
		if(result.isSubmitted()) {
			for(ShipmentItem si: shipment.getShipmentItems()) {
				Long units = si.getSaleItem().getItem().getUnitsOnStock();
				units = units - si.getUnits();
				itemRepo.updateUnitsOnStock(units, si.getSaleItem().getItem().getId());
			}
		}
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/shipment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		shipmentRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
	private byte[] generatePdf(Shipment shipment, boolean submitted) throws IOException, DocumentException {
		String itemQuantity = "";
		String itemDescription = "";
		String itemCases = "";
		String itemPallets = "";
		for (ShipmentItem si : shipment.getShipmentItems()) {
			itemQuantity += si.getUnits() + "\n";
			itemDescription += si.getSaleItem().getItem().getNumber() + " - " +si.getSaleItem().getItem().getName() + "\n";
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
		stamper.getAcroFields().setField("poNumber", shipment.getPoNumber());
		stamper.getAcroFields().setField("shippingDate", shipment.getShippingDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
		stamper.getAcroFields().setField("via", shipment.getVia());
		stamper.getAcroFields().setField("fob", shipment.getFob());
		stamper.getAcroFields().setField("freight", shipment.getFreight().toString());
		stamper.getAcroFields().setField("csNumber", shipment.getCsNumber());

		stamper.getAcroFields().setField("itemQuantity", itemQuantity);
		stamper.getAcroFields().setField("itemDescription", itemDescription);
		stamper.getAcroFields().setField("itemCases", itemCases);
		stamper.getAcroFields().setField("itemPallets", itemPallets);
		String shippingAddress = shipment.getShippingAddress().getStreet() + "\n" +shipment.getShippingAddress().getCity() + ", "+ shipment.getShippingAddress().getState() + " "+shipment.getShippingAddress().getZip();
		stamper.getAcroFields().setField("shippingAddress", shippingAddress);
		String billingAddress = shipment.getCustomer().getBillingAddress().getStreet() + "\n" +shipment.getCustomer().getBillingAddress().getCity() + ", "+ shipment.getCustomer().getBillingAddress().getState() + " "+shipment.getCustomer().getBillingAddress().getZip();		
		stamper.getAcroFields().setField("billingAddress", billingAddress);
		stamper.getAcroFields().setField("notes", shipment.getNotes());
		stamper.getAcroFields().setField("totalUnits", shipment.getTotalUnits().toString());
		stamper.getAcroFields().setField("totalCases", shipment.getTotalCases().toString());
		stamper.getAcroFields().setField("totalPallets", shipment.getTotalPallets().toString());
		stamper.getAcroFields().setField("totalWeight", shipment.getTotalWeight().toString());
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