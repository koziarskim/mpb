package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.InvoiceItemListDto;
import com.noovitec.mpb.entity.InvoiceItem;
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
	
	@GetMapping("/invoiceItem/xls")
	HttpEntity<byte[]> getXls(@RequestParam(required = false) Pageable pageable,
			@RequestParam(required=false) boolean totals,
			@RequestParam(required=false) String invoiceNumber,
			@RequestParam(required=false) Long itemId,
			@RequestParam(required=false) Long saleId,
			@RequestParam(required=false) Long customerId,
			@RequestParam(required=false) Long shipmentId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate invoiceTo,
			@RequestParam(required=false) Long brandId) throws IOException {
		@SuppressWarnings("unchecked")
		Page<InvoiceItem> invoiceItems = (Page<InvoiceItem>) invoiceItemRepo.findPageable(pageable, totals, invoiceNumber, itemId, saleId, customerId, 
				shipmentId, invoiceFrom, invoiceTo, brandId);
		Map<Long, List<InvoiceItem>> customers = new HashMap<Long, List<InvoiceItem>>();
		for(InvoiceItem ii : invoiceItems) {
			Long cuId = ii.getInvoice().getShipment().getCustomer().getId();
			if(customers.get(cuId) == null) {
				customers.put(cuId, new ArrayList<InvoiceItem>());
			}
			customers.get(cuId).add(ii);
		}
		byte[] data = generateXls(customers);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "Invoices" + "-" + sdf.format(timestamp) +".xlsx";
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.set("File-Name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
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
		if(totals) {
			@SuppressWarnings("unchecked")
			Page<?> result = (Page<InvoiceItem>) invoiceItemRepo.findPageable(pageable, totals, invoiceNumber, itemId, saleId, customerId, 
					shipmentId, invoiceFrom, invoiceTo, brandId);
			return result;
		} else {
			@SuppressWarnings("unchecked")
			Page<InvoiceItem> invoiceItems = (Page<InvoiceItem>) invoiceItemRepo.findPageable(pageable, totals, invoiceNumber, itemId, saleId, customerId, 
					shipmentId, invoiceFrom, invoiceTo, brandId);
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

	private byte[] generateXls(Map<Long, List<InvoiceItem>> customers) throws IOException {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY");
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Persons");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 2000);
		sheet.setColumnWidth(2, 3000);
		sheet.setColumnWidth(3, 2000);
		sheet.setColumnWidth(4, 10000);
		sheet.setColumnWidth(5, 2000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 4000);
		 
		Row rowHeader = sheet.createRow(0);	 
		CellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		addCell(0, "Customer", rowHeader);
		addCell(1, "Invoice #", rowHeader);
		addCell(2, "Date", rowHeader);
		addCell(3, "Sale #", rowHeader);
		addCell(4, "Item # (Name)", rowHeader);
		addCell(5, "Qty", rowHeader);
		addCell(6, "Unit Price", rowHeader);
		addCell(7, "Amount", rowHeader);
		
		XSSFFont cellFont = ((XSSFWorkbook) workbook).createFont();
		cellFont.setFontName("Arial");
		cellFont.setBold(false);
		CellStyle cellStyle = workbook.createCellStyle();
		int rowCount = 1;
		for (Map.Entry<Long, List<InvoiceItem>> entry : customers.entrySet()) {
			List<InvoiceItem> invoiceItems = entry.getValue();
			int invoiceItemCount = 0;
			String customerName = "";
			for(InvoiceItem ii: invoiceItems) {
				if(invoiceItemCount == 0) {
					customerName = ii.getInvoice().getShipment().getCustomer().getName();
				} else {
					customerName = "";
				}
				log.info("Customer: "+customerName);
				log.info("II: "+ii.getId());
				Row row = sheet.createRow(rowCount);
				cellStyle.setFont(cellFont);
				addCell(0, customerName, row);
				addCell(1, String.valueOf(ii.getInvoice().getNumber()), row);
				addCell(2, ii.getInvoice().getDate().format(dateFormat), row);
				addCell(3, String.valueOf(ii.getSaleItem().getSale().getNumber()), row);
				addCell(4, String.valueOf(ii.getSaleItem().getItemPackaging().getItem().getNumber()) + " (" + ii.getSaleItem().getItemPackaging().getItem().getName() + ")", row);
				addCell(5, String.valueOf(ii.getUnitsInvoiced()), row);
				addCell(6, String.valueOf(ii.getUnitPrice()), row);
				addCell(7, String.valueOf(ii.getTotalUnitPrice()), row);

				invoiceItemCount++;
				rowCount++;
			}
		};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();
		return baos.toByteArray();
	}

	private void addCell(int column, String value, Row row) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
	}
}