package com.noovitec.mpb.rest;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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

import com.itextpdf.text.DocumentException;
import com.noovitec.mpb.dto.InvoiceListDto;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.repo.InvoiceRepo;
import com.noovitec.mpb.service.InvoiceService;
import com.noovitec.mpb.service.NotificationService;

@RestController
@RequestMapping("/api")
class InvoiceRest {

	private final Logger log = LoggerFactory.getLogger(InvoiceRest.class);
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceRepo invoiceRepo;
	@Autowired
	private NotificationService notificationService;
	
	public InvoiceRest(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping("/invoice/pageable")
	Page<InvoiceListDto> getAllPageable(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required=false) String invoiceNumber,
			@RequestParam(required=false) Long itemId,
			@RequestParam(required=false) Long saleId,
			@RequestParam(required=false) Long customerId,
			@RequestParam(required=false) Long shipmentId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceTo) {
		Page<Invoice> invoices = invoiceRepo.findPagable(pageable, invoiceNumber, itemId, saleId, customerId, shipmentId, invoiceFrom, invoiceTo);
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
		String fileName = "INV_"+invoice.getNumber() +".pdf";
		byte[] data = invoiceService.generatePdf(invoice.getId());
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@PostMapping("/invoice")
	ResponseEntity<?> post(@RequestBody Invoice invoice, 
			@RequestParam(required=false) boolean sendEmail,
			@RequestParam(required=false) boolean includeCc) throws IOException, DocumentException {
		if(!invoice.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invoice Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = invoiceRepo.getIdByNumber(invoice.getNumber());
		if((invoice.getId()==null && id !=null) || (invoice.getId()!=null && id !=null && !invoice.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invoice Number already exists. Please, choose differrent.");
		}
		invoice = invoiceService.save(invoice);
		if(sendEmail) {
			List<String> emails = new ArrayList<String>();
			emails.add(invoice.getInvoiceEmail());
			if(includeCc) {
				emails.add("akoziarski@marketplacebrands.com");
			}
			Map<String, String> model = new HashMap<String, String>();
			model.put("invoiceNumber", invoice.getNumber());
			byte[] data = invoiceService.generatePdf(invoice.getId());
			notificationService.sendMailAttachment(emails, model, Notification.TYPE.INVOICE_EMAIL, data, "MPB_Invoice_"+invoice.getNumber()+".pdf");
			invoice.setSent(true);
			invoice = invoiceService.save(invoice);
		}
		return ResponseEntity.ok().body(invoice);
	}

	@DeleteMapping("/invoice/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		invoiceService.delete(id);
		return ResponseEntity.ok().build();
	}

	

}