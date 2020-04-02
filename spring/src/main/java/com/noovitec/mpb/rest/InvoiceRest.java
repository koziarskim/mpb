package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.InvoiceListDto;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.InvoiceRepo;
import com.noovitec.mpb.service.InvoiceService;

@RestController
@RequestMapping("/api")
class InvoiceRest {

	private final Logger log = LoggerFactory.getLogger(InvoiceRest.class);
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceRepo invoiceRepo;
	
	public InvoiceRest(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping("/invoice/pageable")
	Page<InvoiceListDto> getAllPageable(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required=false) String invoiceNumber,
			@RequestParam(required=false) Long saleId,
			@RequestParam(required=false) Long customerId,
			@RequestParam(required=false) Long shipmentId) {
		Page<Invoice> invoices = invoiceRepo.findPagable(pageable, invoiceNumber, saleId, customerId, shipmentId);
		Page<InvoiceListDto> dtos = invoices.map(invoice -> {
			InvoiceListDto dto = new InvoiceListDto();
			dto.setId(invoice.getId());
			dto.setNumber(invoice.getNumber());
			dto.setDate(invoice.getDate());
			dto.setShippingDate(invoice.getShippingDate());
			dto.setSent(invoice.isSent());
			dto.setType(invoice.getType());
			dto.setShipmentNumber(invoice.getShipment().getNumber());
			dto.setCustomerName(invoice.getShipment().getCustomer().getName());
			return dto;
		});
		return dtos;
	}

	@GetMapping("/invoice/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Invoice> invoice = invoiceRepo.findById(id);
		return invoice.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/invoice/{invoiceId}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long invoiceId) throws DocumentException, IOException {
		Invoice invoice = invoiceRepo.findById(invoiceId).get();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "INV_"+invoice.getNumber()+"_"+invoice.getId() + "-" + sdf.format(timestamp) +".pdf";
		byte[] data = this.generatePdf(invoice, true);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@PostMapping("/invoice")
	ResponseEntity<?> post(@RequestBody Invoice invoice) {
		if(!invoice.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invoice Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = invoiceRepo.getIdByNumber(invoice.getNumber());
		if((invoice.getId()==null && id !=null) || (invoice.getId()!=null && id !=null && !invoice.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invoice Number already exists. Please, choose differrent.");
		}
		invoice = invoiceService.save(invoice);
		return ResponseEntity.ok().body(invoice);
	}

	@DeleteMapping("/invoice/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		invoiceService.delete(id);
		return ResponseEntity.ok().build();
	}

	private byte[] generatePdf(Invoice invoice, boolean submitted) throws IOException, DocumentException {
		Map<String, String> pt = new HashMap<String, String>();
		pt.put("TPB", "TP Bill");
		pt.put("PRP", "Pre Paid");
		pt.put("TPO", "TP Bill Other");
		pt.put("COL","Collect");
		pt.put("CPU", "Customer Pickup");
		String itemSaleNumber = "";
		String itemQuantity = "";
		String itemDescription = "";
		String itemCasePack = "";
		String itemPrice = "";
		String itemTotalPrice = "";
		int totalUnits = 0;
		BigDecimal totalAmount = BigDecimal.ZERO;
		Collection<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
		Map<Long, String> saleIds = new HashMap<Long, String>();
		for (InvoiceItem ii : invoiceItems) {
			saleIds.put(ii.getSaleItem().getSale().getId(), ii.getSaleItem().getSale().getNumber());
			itemSaleNumber += ii.getSaleItem().getSale().getNumber() +"\n\n";
			itemQuantity += ii.getUnitsInvoiced() + "\n\n";
			itemDescription += ii.getSaleItem().getItem().getNumber() + " - " +ii.getSaleItem().getItem().getName()+"\n" 
					+"UPC: "+ii.getSaleItem().getItem().getUpc()+(ii.getSaleItem().getSku()==null?"":", SKU# "+ ii.getSaleItem().getSku()) + "\n";
			itemCasePack += ii.getSaleItem().getItem().getCasePack() + "\n\n";
			itemPrice += ii.getUnitPrice() + "\n\n";
			itemTotalPrice += ii.getTotalUnitPrice() + "\n\n";
			totalUnits += ii.getUnitsInvoiced();
			totalAmount.add(ii.getTotalUnitPrice()==null?BigDecimal.ZERO:ii.getTotalUnitPrice());
		}
		InputStream bolIn = null;
		bolIn = this.getClass().getClassLoader().getResourceAsStream("pdf/Invoice-Template-1.pdf");
		PdfReader bolTemplate = new PdfReader(bolIn);
		ByteArrayOutputStream bolBaos = new ByteArrayOutputStream();
		PdfStamper bolStamper = new PdfStamper(bolTemplate, bolBaos);
		bolStamper.setFormFlattening(true);
		bolStamper.getAcroFields().setField("date", (invoice.getDate()==null?"":invoice.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy"))));
		bolStamper.getAcroFields().setField("number", invoice.getNumber());
		Shipment shipment = invoice.getShipment();
		Customer customer = shipment.getCustomer();
		String saleNumber = "Multiple";
		if(saleIds.size()==1) {
			saleNumber = shipment.getShipmentItems().iterator().next().getSaleItem().getSale().getNumber();
		}
		bolStamper.getAcroFields().setField("saleNumber", saleNumber);
		if(customer.getBillingAddress()!=null) {
			String billingAddress = shipment.getFreightAddress().getDc() + "\n"
				+ customer.getBillingAddress().getStreet() + "\n" 
				+ customer.getBillingAddress().getCity() + ", "+ customer.getBillingAddress().getState() + " "+customer.getBillingAddress().getZip();		
			bolStamper.getAcroFields().setField("billingAddress", billingAddress);
		}
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
		bolStamper.getAcroFields().setField("paymentTerms",  pt.get(invoice.getPaymentTerms()));
		bolStamper.getAcroFields().setField("shippingDate", invoice.getShippingDate()==null?"":invoice.getShippingDate().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
		
		bolStamper.getAcroFields().setField("via", invoice.getVia());
		bolStamper.getAcroFields().setField("fob", invoice.getFob());
		bolStamper.getAcroFields().setField("loadNumber", invoice.getLoadNumber());

		bolStamper.getAcroFields().setField("itemSaleNumber", itemSaleNumber);
		bolStamper.getAcroFields().setField("itemQuantity", itemQuantity);
		bolStamper.getAcroFields().setField("itemDescription", itemDescription);
		bolStamper.getAcroFields().setField("itemCasePack", itemCasePack);
		bolStamper.getAcroFields().setField("itemPrice", itemPrice);
		bolStamper.getAcroFields().setField("itemTotalPrice", itemTotalPrice);

		bolStamper.getAcroFields().setField("totalUnits", String.valueOf(totalUnits));
		bolStamper.getAcroFields().setField("totalCases", "TODO");
		bolStamper.getAcroFields().setField("totalPallets", "TODO");
		bolStamper.getAcroFields().setField("balanceDue", "TODO");
		bolStamper.getAcroFields().setField("totalAmount", totalAmount.toString());
		
		bolStamper.close();
		bolTemplate.close();
		
		return bolBaos.toByteArray();
	}

}