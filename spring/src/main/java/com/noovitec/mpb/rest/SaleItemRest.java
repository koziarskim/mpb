package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleItemDto;
import com.noovitec.mpb.entity.Packaging;
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
			@RequestParam(required = false) String customFilter,
			@RequestParam(required = false) boolean showAll) {
		if(totals) {
			Page<?> result = saleItemRepo.findPageable(pageable, totals, numberName, saleId, customerId, itemId, packagingId, status, customFilter, showAll);
			return result;
		}else {
			@SuppressWarnings("unchecked")
			Page<SaleItem> saleItems = (Page<SaleItem>) saleItemRepo.findPageable(pageable, totals, numberName, saleId, customerId, itemId, packagingId, status, customFilter, showAll);
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
				dto.setApproved(saleItem.getSale().isApproved());
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
	
	@GetMapping("/saleItem/{id}/carton/pdf")
	HttpEntity<byte[]> getCartonPdf(
			@PathVariable Long id,
			@RequestParam int pageFrom,
			@RequestParam int pageTo) throws DocumentException, IOException {
		SaleItem saleItem = saleItemRepo.findById(id).get();
		String fileName = "Carton_"+saleItem.getSale().getNumber()+"_"+saleItem.getItemPackaging().getItem().getNumber()+".pdf";
		byte[] data = this.generateCartonPdf(saleItem, pageFrom, pageTo);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/saleItem/{id}/tag/pdf")
	HttpEntity<byte[]> getTagPdf(
			@PathVariable Long id,
			@RequestParam int pageFrom,
			@RequestParam int pageTo) throws DocumentException, IOException {
		SaleItem saleItem = saleItemRepo.findById(id).get();
		String fileName = "Tag"+saleItem.getSale().getNumber()+"_"+saleItem.getItemPackaging().getItem().getNumber()+".pdf";
		byte[] data = this.generateTagPdf(saleItem, pageFrom, pageTo);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("content-disposition", "inline; filename=" + fileName);
		header.set("file-name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	private byte[] generateCartonPdf(SaleItem saleItem, int pageFrom, int pageTo) throws IOException, DocumentException {
	    Document doc = new Document();
	    ByteArrayOutputStream mainBaos = new ByteArrayOutputStream();
	    PdfSmartCopy copy = new PdfSmartCopy(doc, mainBaos);
	    doc.open();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("pdf/Carton-Label.pdf");
		PdfReader mainReader = new PdfReader(in);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(false);
	    for(int i = pageFrom; i <= pageTo; i++) {
	    	PdfReader reader = new PdfReader(mainReader);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfStamper stamper = new PdfStamper(reader, baos);
	        String customerName = saleItem.getSale().getCustomer().getName();
//	        if(customerName.length() > 20) {
//	        	customerName = saleItem.getSale().getCustomer().getName().substring(0,20)+"...";
//	        }
	        stamper.getAcroFields().setField("customerName", customerName);
	        if(saleItem.getSale().getShippingAddress() != null) {
		        stamper.getAcroFields().setField("dc", saleItem.getSale().getShippingAddress().getDc());
		        String shipToAddress = ((saleItem.getSale().getShippingAddress().getLine()==null || saleItem.getSale().getShippingAddress().getLine().isBlank())?"":saleItem.getSale().getShippingAddress().getLine()+"\n")
		        		+saleItem.getSale().getShippingAddress().getStreet()+"\n"
		        		+saleItem.getSale().getShippingAddress().getCity()+", "
		        		+saleItem.getSale().getShippingAddress().getState()+" "
		        		+saleItem.getSale().getShippingAddress().getZip();
		        stamper.getAcroFields().setField("shipToAddress", shipToAddress);
	        }
	        stamper.getAcroFields().setField("saleNumber", "PO# "+saleItem.getSale().getNumber());
	        stamper.getAcroFields().setField("department", (saleItem.getDepartment()==null || saleItem.getDepartment().isBlank())?"":"DEPT. "+saleItem.getDepartment());
	        String itemNumber = saleItem.getItemPackaging().getItem().getNumber()+"-"+saleItem.getItemPackaging().getItem().getName();
	        if(itemNumber.length() > 25) {
	        	itemNumber = itemNumber.substring(0,25)+"...";
        	}
	        stamper.getAcroFields().setField("itemNumber", itemNumber);
	        stamper.getAcroFields().setField("sku", (saleItem.getSku()==null || saleItem.getSku().isBlank())?"":"SKU: "+saleItem.getSku());
	        stamper.getAcroFields().setField("upc", "UPC: "+String.valueOf(saleItem.getItemPackaging().getItem().getUpc().getCode()));
	        stamper.getAcroFields().setField("casePack", "Case Pack: "+String.valueOf(saleItem.getItemPackaging().getPackaging().getCasePack()));
	        BigDecimal grossWeight = saleItem.getItemPackaging().getItem().getWeight().multiply(BigDecimal.valueOf(saleItem.getItemPackaging().getPackaging().getCasePack()));
	        grossWeight = grossWeight.add(saleItem.getItemPackaging().getPackaging().getCaseWeight());
	        stamper.getAcroFields().setField("grossWeight", grossWeight.round(new MathContext(0, RoundingMode.CEILING)).intValue()+" LBS");
	        BigDecimal caseCube = saleItem.getItemPackaging().getPackaging().getCaseHeight().multiply(saleItem.getItemPackaging().getPackaging().getCaseLength())
	        		.multiply(saleItem.getItemPackaging().getPackaging().getCaseWidth()).divide(BigDecimal.valueOf(1728),2, RoundingMode.CEILING);
	        stamper.getAcroFields().setField("caseCube", df.format(caseCube.round(new MathContext(2, RoundingMode.CEILING)))+" FT3");
	        BigDecimal totalCases = BigDecimal.valueOf(saleItem.getUnits()).divide(BigDecimal.valueOf(saleItem.getItemPackaging().getPackaging().getCasePack()),2, RoundingMode.CEILING);
	        stamper.getAcroFields().setField("totalCases", String.valueOf(totalCases.round(new MathContext(0, RoundingMode.CEILING)).intValue()));
	        stamper.getAcroFields().setField("totalUnits", String.valueOf(saleItem.getUnits()));
	        stamper.getAcroFields().setField("expiration", "Best By: "+saleItem.getExpiration().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
	        stamper.getAcroFields().setField("page", String.valueOf(i)+" of "+pageTo);
	        stamper.setFormFlattening(true);
	        stamper.close();
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	        reader.close();
	        baos.close();
	    }
	    doc.close();
	    return mainBaos.toByteArray();
	}
	
	private byte[] generateTagPdf(SaleItem saleItem, int pageFrom, int pageTo) throws IOException, DocumentException {
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
        int totalCases = BigDecimal.valueOf(saleItem.getUnits()).divide(BigDecimal.valueOf(saleItem.getItemPackaging().getPackaging().getCasePack()),2, RoundingMode.CEILING)
        		.round(new MathContext(0, RoundingMode.CEILING)).intValue();
        int casesPerPallet = saleItem.getItemPackaging().getPackaging().getTi() * saleItem.getItemPackaging().getPackaging().getHi();
	    for(int i = pageFrom; i <= pageTo; i++) {
	    	PdfReader reader = new PdfReader(mainReader);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfStamper stamper = new PdfStamper(reader, baos);
	        String customerName = saleItem.getSale().getCustomer().getName();
//	        if(customerName.length() > 20) {
//	        	customerName = saleItem.getSale().getCustomer().getName().substring(0,20)+"...";
//	        }
	        stamper.getAcroFields().setField("customerName", customerName);
	        String location = (saleItem.getSale().getShippingAddress().getLocationName()==null || saleItem.getSale().getShippingAddress().getLocationName().isBlank())?"":"LOCATION ID. "+saleItem.getSale().getShippingAddress().getLocationName();
	        String dc = saleItem.getSale().getShippingAddress().getDc();
	        String shipToAddress = location+"\n"
	        		+dc+"\n"
	        		+saleItem.getSale().getShippingAddress().getStreet()+"\n"
	        		+(saleItem.getSale().getShippingAddress().getLine()==null?"":saleItem.getSale().getShippingAddress().getLine()+"\n")
	        		+saleItem.getSale().getShippingAddress().getCity()+", "
	        		+saleItem.getSale().getShippingAddress().getState()+" "
	        		+saleItem.getSale().getShippingAddress().getZip();
	        stamper.getAcroFields().setField("shipToAddress", shipToAddress);
	        stamper.getAcroFields().setField("saleNumber", "PO# "+saleItem.getSale().getNumber());
	        String itemNumber = saleItem.getItemPackaging().getItem().getNumber()+"-"+saleItem.getItemPackaging().getItem().getName();
	        if(itemNumber.length() > 35) {
	        	itemNumber = itemNumber.substring(0,35)+"...";
        	}
	        stamper.getAcroFields().setField("itemNumber", itemNumber);
	        stamper.getAcroFields().setField("casePack", "Case Pack: "+String.valueOf(saleItem.getItemPackaging().getPackaging().getCasePack()));
	        stamper.getAcroFields().setField("sku", (saleItem.getSku()==null || saleItem.getSku().isBlank())?"":"SKU: "+saleItem.getSku());
	        stamper.getAcroFields().setField("expiration", "Best By: "+saleItem.getExpiration().format(DateTimeFormatter.ofPattern("MM/dd/yyy")));
	        int cases = 0;
	        if(totalCases < casesPerPallet) {
	        	cases = totalCases;
	        } else if (totalCases >= (i * casesPerPallet)) {
	        	cases = casesPerPallet;
	        } else {
	        	cases = casesPerPallet - ((i * casesPerPallet) - totalCases);
	        }
	        stamper.getAcroFields().setField("cases", "Total Cartons/Cases: "+String.valueOf(cases));
	        stamper.getAcroFields().setField("page", "Pallet "+String.valueOf(i)+" of "+pageTo);
	        stamper.setFormFlattening(true);
	        stamper.close();
	        reader = new PdfReader(baos.toByteArray());
	        copy.addPage(copy.getImportedPage(reader, 1));
	    }
	    doc.close();
	    return mainBaos.toByteArray();
	}

}