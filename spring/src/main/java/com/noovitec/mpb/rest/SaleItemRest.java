package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class SaleItemRest {

	private final Logger log = LoggerFactory.getLogger(SaleItemRest.class);
	private SaleItemRepo saleItemRepo;

	@Autowired
	CrudService crudService;
	@Autowired
	SaleService saleService;

	public SaleItemRest(SaleItemRepo saleItemRepo) {
		this.saleItemRepo = saleItemRepo;
	}

	@GetMapping("/saleItem")
	List<SaleItem> getAll(@RequestParam(name = "ids", required = false) Long[] ids) {
		return saleItemRepo.findAllByIds(ids);
	}

	@GetMapping("/saleItem/pageable")
	Page<?> getAllPageable(
			@RequestParam Pageable pageable, 
			@RequestParam(required = false) boolean totals,
			@RequestParam(required = false) String numberName,
			@RequestParam(required = false) Long saleId,
			@RequestParam(required = false) Long itemId, 
			@RequestParam(required = false) Long packagingId, 
			@RequestParam(required = false) Long customerId, 
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String unitsFilter,
			@RequestParam(required = false) boolean showAll) {
		@SuppressWarnings("unchecked")
		Page<SaleItem> saleItems = (Page<SaleItem>) saleItemRepo.findPageable(pageable, totals, numberName, saleId, customerId, itemId, packagingId, status, unitsFilter, showAll);
		if(totals) {
			Page<?> result = saleItemRepo.findPageable(pageable, totals, numberName, saleId, customerId, itemId, packagingId, status, unitsFilter, showAll);
			return result;
		}else {
			Page<SaleItemDto> all = saleItems.map(saleItem -> {
				SaleItemDto dto = new SaleItemDto();
				dto.setId(saleItem.getId());
				dto.setSaleId(saleItem.getSale().getId());
				dto.setSaleNumber(saleItem.getSale().getNumber());
				dto.setSaleName(saleItem.getSale().getName());
				dto.setItemId(saleItem.getItemPackaging().getItem().getId());
				dto.setItemNumber(saleItem.getItemPackaging().getItem().getNumber());
				dto.setItemName(saleItem.getItemPackaging().getItem().getName());
				dto.setCustomerId(saleItem.getSale().getCustomer().getId());
				dto.setCustomerName(saleItem.getSale().getCustomer().getName());
				dto.setDc(saleItem.getSale().getShippingAddress() != null
						? saleItem.getSale().getShippingAddress().getDc() + " (" + saleItem.getSale().getShippingAddress().getState()
						: "");
				dto.setUnitsSold(Long.valueOf(saleItem.getUnits()));
				dto.setUnitsScheduled(saleItem.getUnitsScheduled());
				dto.setUnitsProduced(saleItem.getUnitsProduced());
				dto.setUnitsShipped(saleItem.getUnitsShipped());
				dto.setUnitsAdjusted(saleItem.getUnitsAdjusted());
				dto.setUnitsAssigned(saleItem.getUnitsAssigned());
				dto.setStatus(saleItem.getStatus());
				dto.setPackagingLabel(saleItem.getItemPackaging().getLabel());
				return dto;
			});
			return all;
		}
	}

	@GetMapping("/saleItem/{id}")
	ResponseEntity<SaleItem> getSaleItem(@PathVariable Long id) {
		Optional<SaleItem> result = saleItemRepo.getSaleItemById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/saleItem/kv/customer/{customerId}")
	List<KeyValueDto> findKvByCustomer(@PathVariable Long customerId) {
		List<KeyValueDto> dtos = saleItemRepo.findKvByCustomer(customerId);
		return dtos;
	}

	@GetMapping("/saleItem/sale/{sale_id}")
	Collection<KeyValueDto> getAllBySale(@PathVariable Long sale_id) {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findSaleItemsBySale(sale_id);
		return saleDtos;
	}

	@GetMapping("/saleItem/kv/transfer/item/{item_id}")
	List<KeyValueDto> getAllTrasferByItem(@PathVariable Long item_id) {
		List<SaleItem> saleItems = saleItemRepo.findKvTrasferByItem(item_id);
		List<KeyValueDto> dtos = new ArrayList<KeyValueDto>();
		for(SaleItem saleItem: saleItems) {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(saleItem.getId());
			dto.setName(saleItem.getSale().getNumber() + "("+saleItem.getSale().getCustomer().getName()+"), Assigned: "+saleItem.getUnitsAssigned());
			dtos.add(dto);
		}		

		return dtos;
	}
	
	@GetMapping("/saleItem/kv/item/{item_id}")
	Collection<KeyValueDto> getAllByItem(@PathVariable Long item_id) {
		Collection<KeyValueDto> dtos = saleItemRepo.findKvByItem(item_id);
		return dtos;
	}

	@GetMapping("/saleItem/kv/item/{itemId}/customer/{customerId}")
	Collection<KeyValueDto> getAllByItemAndCustomer(@PathVariable Long itemId, @PathVariable Long customerId) {
		Collection<KeyValueDto> dtos = saleItemRepo.findKvByItemAndCustomer(itemId, customerId);
		return dtos;
	}

	@GetMapping("/saleItem/kv")
	Collection<KeyValueDto> getAllKvs() {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findAllKvs();
		return saleDtos;
	}

	@GetMapping("/saleItem/kv/shipment/{shipmentId}")
	Collection<KeyValueDto> getAllKvsByPurchase(@PathVariable Long shipmentId) {
		Collection<KeyValueDto> saleDtos = saleItemRepo.findAllKvsByShipment(shipmentId);
		return saleDtos;
	}

	@PostMapping("/saleItem/{saleItemId}/move/to/sale/{saleId}")
	ResponseEntity<?> moveSaleItem(@PathVariable Long saleItemId, @PathVariable Long saleId) {
		SaleItem si = saleItemRepo.getSaleItemById(saleItemId).get();
		Long oldSaleId = si.getSale().getId();
		saleItemRepo.moveSaleItem(saleItemId, saleId);
		saleService.updateUnits(Arrays.asList(oldSaleId, saleId));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/saleItem/{id}/pdf")
	HttpEntity<byte[]> getPdf(@PathVariable Long id) throws DocumentException, IOException {
		SaleItem saleItem = saleItemRepo.findById(id).get();
		String fileName = "PO_"+saleItem.getSale().getNumber()+"_"+saleItem.getSale().getName() +".pdf";
		byte[] data = this.generatePdf(saleItem);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	private byte[] generatePdf(SaleItem saleItem) throws IOException, DocumentException {
	    Document doc = new Document();
	    ByteArrayOutputStream mainBaos = new ByteArrayOutputStream();
	    PdfSmartCopy copy = new PdfSmartCopy(doc, mainBaos);
	    doc.open();
	    
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/Carton-Label.pdf");
		PdfReader mainReader = new PdfReader(in);
	    
	    PdfReader reader;
	    ByteArrayOutputStream baos;
	    PdfStamper stamper;
	    AcroFields form;
	    int totalPages = 500;
	    for(int i = 1; i <= totalPages; i++) {
	        
	        reader = new PdfReader(mainReader);
	        baos = new ByteArrayOutputStream();
	        stamper = new PdfStamper(reader, baos);
	        form = stamper.getAcroFields();
	        
	        //methods to fill forms
	        String page = "Page "+String.valueOf(i)+" of "+totalPages;
	        stamper.getAcroFields().setField("page", page);
	        stamper.setFormFlattening(true);
	        stamper.close();
	        
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	    }

	    doc.close();
	    return mainBaos.toByteArray();
	}
		
		
		
		
//		Document document = new Document();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PdfCopy copy = new PdfSmartCopy(document, baos);
//		document.open();
//
//		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/Carton-Label.pdf");
//		PdfReader pdfTemplate = new PdfReader(in);
//
//	    
//
//		PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
//		stamper.setFormFlattening(true);
//		stamper.getAcroFields().setField("page", "1");
//		PdfImportedPage importedPage = copy.getImportedPage(pdfTemplate, 1);
//		copy.addPage(importedPage);
//		stamper.close();
//		pdfTemplate.close();
//		return baos.toByteArray();
//	}
	
}