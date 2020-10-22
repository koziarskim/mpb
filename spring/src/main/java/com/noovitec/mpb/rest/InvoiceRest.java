package com.noovitec.mpb.rest;

import java.io.IOException;
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
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.repo.InvoiceRepo;
import com.noovitec.mpb.service.InvoiceService;
import com.noovitec.mpb.service.NotificationService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class InvoiceRest {

	private final Logger log = LoggerFactory.getLogger(InvoiceRest.class);
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceRepo invoiceRepo;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private SaleService saleService;
	
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
			dto.setTotalAmount(invoice.getTotalAmount());
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
		String fileName = invoice.getNumber() +".pdf";
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
			@RequestParam(required=false) boolean includeCc,
			@RequestParam(required=false) String emailSubject,
			@RequestParam(required=false) String emailBody) throws IOException, DocumentException {
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
			Map<String, String> model = new HashMap<String, String>();
			model.put("invoiceNumber", invoice.getNumber());
			byte[] data = invoiceService.generatePdf(invoice.getId());
			notificationService.sendMailAttachment(emails, model, Notification.TYPE.INVOICE_EMAIL, data, invoice.getNumber()+".pdf", emailSubject, emailBody);
			invoice.setSent(true);
			invoice = invoiceService.save(invoice);
			if(includeCc) {
				List<String> ccEmails = new ArrayList<String>();
				ccEmails.add("akoziarski@marketplacebrands.com");
				ccEmails.add("mkoziarski@marketplacebrands.com");
				notificationService.sendMailAttachment(ccEmails, model, Notification.TYPE.INVOICE_EMAIL, data, invoice.getNumber()+".pdf", emailSubject, emailBody);
			}
		}
		List<Long> saleIds = new ArrayList<Long>();
		for(InvoiceItem ii: invoice.getInvoiceItems()) {
			saleIds.add(ii.getSaleItem().getSale().getId());
		}
		saleService.updateUnits(saleIds);
		return ResponseEntity.ok().body(invoice);
	}

	@DeleteMapping("/invoice/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Invoice invoice = invoiceRepo.findById(id).get();
		List<Long> saleIds = new ArrayList<Long>();
		for(InvoiceItem ii: invoice.getInvoiceItems()) {
			saleIds.add(ii.getSaleItem().getSale().getId());
		}
		invoiceService.delete(id);
		saleService.updateUnits(saleIds);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/invoice/updateTotals")
	ResponseEntity<?> postUpdate() {
		try {
			List<Invoice> invoices = (List<Invoice>) invoiceRepo.findAll();
			for(Invoice invoice: invoices) {
				invoiceService.save(invoice);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}

}