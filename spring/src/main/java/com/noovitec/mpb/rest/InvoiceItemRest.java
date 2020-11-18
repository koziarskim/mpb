package com.noovitec.mpb.rest;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.InvoiceItemListDto;
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Receiving;
import com.noovitec.mpb.repo.InvoiceItemRepo;
import com.noovitec.mpb.service.InvoiceService;

@RestController
@RequestMapping("/api")
class InvoiceItemRest {

	private final Logger log = LoggerFactory.getLogger(InvoiceItemRest.class);
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceItemRepo invoiceItemRepo;
	
	public InvoiceItemRest(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping("/invoiceItem/pageable")
	Page<?> getAllPageable(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required=false) boolean totals,
			@RequestParam(required=false) String invoiceNumber,
			@RequestParam(required=false) Long itemId,
			@RequestParam(required=false) Long saleId,
			@RequestParam(required=false) Long customerId,
			@RequestParam(required=false) Long shipmentId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceTo,
			@RequestParam(required=false) Long brandId) {
		@SuppressWarnings("unchecked")
		Page<InvoiceItem> invoiceItems = (Page<InvoiceItem>) invoiceItemRepo.findPageable(pageable, totals, invoiceNumber, itemId, saleId, customerId, 
				shipmentId, invoiceFrom, invoiceTo, brandId);
		if(totals) {
			@SuppressWarnings("unchecked")
			Page<?> result = (Page<InvoiceItem>) invoiceItemRepo.findPageable(pageable, totals, invoiceNumber, itemId, saleId, customerId, 
					shipmentId, invoiceFrom, invoiceTo, brandId);
			return result;
		} else {
			Page<InvoiceItemListDto> dtos = invoiceItems.map(invoiceItem -> {
				InvoiceItemListDto dto = new InvoiceItemListDto();
				dto.setId(invoiceItem.getInvoice().getId());
				dto.setNumber(invoiceItem.getInvoice().getNumber());
				dto.setItemNumber(invoiceItem.getSaleItem().getItemPackaging().getItem().getNumber());
				dto.setItemName(invoiceItem.getSaleItem().getItemPackaging().getItem().getName());
				dto.setSaleNumber(invoiceItem.getSaleItem().getSale().getNumber());
				dto.setDate(invoiceItem.getInvoice().getDate());
				dto.setShippingDate(invoiceItem.getInvoice().getShippingDate());
				dto.setSent(invoiceItem.getInvoice().isSent());
				dto.setType(invoiceItem.getInvoice().getType());
				dto.setShipmentNumber(invoiceItem.getInvoice().getShipment().getNumber());
				dto.setCustomerName(invoiceItem.getInvoice().getShipment().getCustomer().getName());
				dto.setUnitsInvoiced(invoiceItem.getUnitsInvoiced());
				dto.setUnitPrice(invoiceItem.getUnitPrice());
				dto.setTotalUnitPrice(invoiceItem.getTotalUnitPrice());
				dto.setBrandName(invoiceItem.getSaleItem().getItemPackaging().getItem().getBrand() == null ? "" : invoiceItem.getSaleItem().getItemPackaging().getItem().getBrand().getName());
				return dto;
			});
			return dtos;
		}
	}

}