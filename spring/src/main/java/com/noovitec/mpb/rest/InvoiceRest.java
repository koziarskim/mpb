package com.noovitec.mpb.rest;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.InvoiceListDto;
import com.noovitec.mpb.entity.Invoice;
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
	Page<InvoiceListDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable) {
		Page<Invoice> invoices = invoiceRepo.findPagable(pageable);
		Page<InvoiceListDto> dtos = invoices.map(invoice -> {
			InvoiceListDto dto = new InvoiceListDto();
			dto.setId(invoice.getId());
			dto.setNumber(invoice.getNumber());
			dto.setDate(invoice.getDate());
			dto.setShippingDate(invoice.getShippingDate());
			dto.setSent(invoice.isSent());
			return dto;
		});
		return dtos;
	}

	@GetMapping("/invoice/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Invoice> invoice = invoiceRepo.findById(id);
		return invoice.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

}