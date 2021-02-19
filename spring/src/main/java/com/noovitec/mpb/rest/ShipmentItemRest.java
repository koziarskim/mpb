package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.PalletTagDto;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.ShipmentItemRepo;

@RestController
@RequestMapping("/api")
class ShipmentItemRest {

	private final Logger log = LoggerFactory.getLogger(ShipmentItemRest.class);
	private ShipmentItemRepo shipmentItemRepo;

	public ShipmentItemRest(ShipmentItemRepo shipmentItemRepo) {
		this.shipmentItemRepo = shipmentItemRepo;
	}

	@GetMapping("/shipmentItem")
	Collection<ShipmentItem> getAll() {
		return shipmentItemRepo.findAll();
	}

	@GetMapping("/shipmentItem/{id}")
	ResponseEntity<ShipmentItem> get(@PathVariable Long id) {
		Optional<ShipmentItem> result = shipmentItemRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/shipmentItem/kv/shipment/{shipmentId}")
	ResponseEntity<List<KeyValueDto>> findKvByShipment(@PathVariable Long shipmentId) {
		List<KeyValueDto> kvs = shipmentItemRepo.findKvByShipment(shipmentId);
		return ResponseEntity.ok().body(kvs);
	}
	
	@PutMapping("/shipmentItem/{id}/tag/pdf")
	HttpEntity<byte[]> getTagPdf(@RequestBody(required = false) PalletTagDto palletTagDto,
			@PathVariable Long id,
			@RequestParam int pageFrom,
			@RequestParam int pageTo) throws DocumentException, IOException {
		ShipmentItem shipItem = shipmentItemRepo.getOne(id);
		String fileName = "Tag_"+palletTagDto.getSaleNumber()+"_"+palletTagDto.getItemNumber()+".pdf";
		byte[] data = this.generateTagPdf(shipItem, palletTagDto);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}


	@PostMapping("/shipmentItem")
	ResponseEntity<ShipmentItem> post(@RequestBody(required = false) ShipmentItem shipmentItem) throws URISyntaxException {
		if (shipmentItem == null) {
			shipmentItem = new ShipmentItem();
		}
		ShipmentItem result = shipmentItemRepo.save(shipmentItem);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/shipmentItem/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		shipmentItemRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
	private byte[] generateTagPdf(ShipmentItem shipItem, PalletTagDto palletTagDto) throws IOException, DocumentException {
	    Document doc = new Document();
	    ByteArrayOutputStream mainBaos = new ByteArrayOutputStream();
	    PdfSmartCopy copy = new PdfSmartCopy(doc, mainBaos);
	    doc.open();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/Tag-Label.pdf");
		PdfReader mainReader = new PdfReader(in);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(false);
        int totalCases = palletTagDto.getCases();;
        int casesPerPallet = Integer.valueOf(palletTagDto.getTi()) * Integer.valueOf(palletTagDto.getHi());
	    for(int i = palletTagDto.getPageFrom(); i <= palletTagDto.getPageTo(); i++) {
	    	PdfReader reader = new PdfReader(mainReader);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfStamper stamper = new PdfStamper(reader, baos);
	        stamper.getAcroFields().setField("customerName", palletTagDto.getCustomerName());
	        String shipToAddress = (palletTagDto.getLocationName()==null?"\n":palletTagDto.getLocationName()+"\n")
	        		+palletTagDto.getDc()+"\n"
	        		+palletTagDto.getStreet()+"\n"
	        		+(palletTagDto.getLine1()==null?"\n":palletTagDto.getLine1()+"\n")
	        		+palletTagDto.getCity()+", "
	        		+palletTagDto.getState()+" "
	        		+palletTagDto.getZip();
	        stamper.getAcroFields().setField("shipToAddress", shipToAddress);
	        stamper.getAcroFields().setField("saleNumber", "PO# "+palletTagDto.getSaleNumber());
	        String itemNumber = palletTagDto.getItemNumber();
	        if(itemNumber.length() > 35) {
	        	itemNumber = itemNumber.substring(0,35)+"...";
        	}
	        stamper.getAcroFields().setField("itemNumber", itemNumber);
	        stamper.getAcroFields().setField("casePack", "Case Pack: "+String.valueOf(palletTagDto.getCasePack()));
	        stamper.getAcroFields().setField("sku", (palletTagDto.getSku()==null || palletTagDto.getSku().isBlank())?"":"SKU: "+palletTagDto.getSku());
	        stamper.getAcroFields().setField("expiration", "Best By: "+palletTagDto.getExpiration().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
	        int cases = 0;
	        if(totalCases < casesPerPallet) {
	        	cases = totalCases;
	        } else if (totalCases >= (i * casesPerPallet)) {
	        	cases = casesPerPallet;
	        } else {
	        	cases = casesPerPallet - ((i * casesPerPallet) - totalCases);
	        }
	        stamper.getAcroFields().setField("cases", "Total Cartons/Cases: "+String.valueOf(cases));
	        stamper.getAcroFields().setField("page", "Pallet "+String.valueOf(i)+" of "+palletTagDto.getPageFrom());
	        stamper.setFormFlattening(true);
	        stamper.close();
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	    }
	    doc.close();
	    return mainBaos.toByteArray();
	}
	
}