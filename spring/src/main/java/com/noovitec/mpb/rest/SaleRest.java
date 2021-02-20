package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.SaleListDto;
import com.noovitec.mpb.entity.Address;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.AddressRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.CustomerService;
import com.noovitec.mpb.service.ItemService;
import com.noovitec.mpb.service.SaleService;

@RestController
@RequestMapping("/api")
class SaleRest {

	@Autowired
	private SaleRepo saleRepo;
	@Autowired
	ItemRepo itemRepo;
	@Autowired
	ScheduleEventRepo scheduleEventRpo;
	@Autowired
	CrudService crudService;
	@Autowired
	CustomerService customerService;
	@Autowired
	ComponentService componentService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SaleItemRepo saleItemRepo;
	@Autowired
	private AddressRepo addressRepo;
	
	private final Logger log = LoggerFactory.getLogger(SaleRest.class);
	private SaleService saleService;

	public SaleRest(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping("/sale")
	Collection<Sale> getAll() {
		return saleRepo.findAll();
	}

	@GetMapping("/sale/pageable")
	Page<?> getAllPageable(@RequestParam(required = false) Pageable pageable, 
			@RequestParam(required = false) boolean totals,
			@RequestParam(required = false) String saleNumber,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) Long customerId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String customFilter,
			@RequestParam(required = false) boolean showAll) {
		if(totals) {
			Page<?> resultTotals = saleRepo.findPageable(pageable, totals, saleNumber, itemId, customerId, status, customFilter, showAll);
			return resultTotals;
		} else {
			@SuppressWarnings("unchecked")
			Page<Sale> sales = (Page<Sale>) saleRepo.findPageable(pageable, totals, saleNumber, itemId, customerId, status, customFilter, showAll);
			DateTimeFormatter windowFormat = DateTimeFormatter.ofPattern("MM/dd");
			Page<SaleListDto> all = sales.map(sale -> {
				SaleListDto dto = new SaleListDto();
				dto.setId(sale.getId());
				dto.setNumber(sale.getNumber());
				dto.setName(sale.getName());
				dto.setDc(sale.getShippingAddress() == null ? "" : sale.getShippingAddress().getDc() + " (" + sale.getShippingAddress().getState() + ")");
				dto.setDate(sale.getDate());
				dto.setCustomerName(sale.getCustomer() == null ? "" : sale.getCustomer().getName());
				dto.setUnitsSold(sale.getUnitsSold());
				dto.setUnitsScheduled(sale.getUnitsScheduled());
				dto.setUnitsProduced(sale.getUnitsProduced());
				dto.setUnitsShipped(sale.getUnitsShipped());
	//			dto.setUnitsOnStock(sale.getUnitsOnStock());
				dto.setUnitsAdjusted(sale.getUnitsAdjusted());
				dto.setUnitsAssigned(sale.getUnitsAssigned());
	//			dto.setInvoicedAmount(sale.getInvoicedAmount());
				dto.setStatus(sale.getStatus());
				String shippingFrom = sale.getShippingFrom()==null?"":sale.getShippingFrom().format(windowFormat);
				String shippingTo = sale.getShippingTo()==null?"":sale.getShippingTo().format(windowFormat);
				dto.setShippingWindow(shippingFrom +"-"+shippingTo);
				return dto;
			});
			return all;
		}
	}

	@GetMapping("/sale/xls")
	HttpEntity<byte[]> getXls(@RequestParam(required = false) Pageable pageable, 
			@RequestParam(required = false) boolean totals,
			@RequestParam(required = false) String saleNumber,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) Long customerId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String customFilter,
			@RequestParam(required = false) boolean showAll) throws IOException {
		@SuppressWarnings("unchecked")
		Page<Sale> sales = (Page<Sale>) saleRepo.findPageable(pageable, totals, saleNumber, itemId, customerId, status, customFilter, showAll);
		List<Long> saleIds = new ArrayList<Long>();
		for(Sale sale: sales) {
			saleIds.add(sale.getId());
		}
		byte[] data = generateXls(saleIds);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String fileName = "Sales_" + "-" + sdf.format(timestamp) +".xlsx";
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + fileName);
		header.set("File-Name", fileName);
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/sale/kv/customer/{customer_id}")
	Collection<KeyValueDto> findKvByCustomer(
			@PathVariable Long customer_id,
			@RequestParam(required=false) boolean onlyStock) {
		List<KeyValueDto> dtos = onlyStock?saleRepo.findKvByCustomerAndStock(customer_id):saleRepo.findKvByCustomer(customer_id);
		return dtos;
	}

	@GetMapping("/sale/kv")
	Collection<KeyValueDto> getKvs() {
		return saleRepo.findKvs();
	}

	@GetMapping("/sale/{id}")
	ResponseEntity<Sale> get(@PathVariable Long id) {
		Optional<Sale> result = saleRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/sale/customer/{customer_id}")
	Collection<Sale> getAllByCustomer(@PathVariable Long customer_id) {
		Collection<Sale> sales = saleRepo.findSaleByCustomer(customer_id);
		return sales;
	}

	@GetMapping("/sale/saleItem/{saleItemId}")
	Sale getBySaleItem(@PathVariable Long saleItemId) {
		Sale sale = saleRepo.getBySaleItem(saleItemId);
		return sale;
	}

	@GetMapping("/sale/validate/number/{number}")
	ResponseEntity<?> validateNumber(@PathVariable String number) {
		Long id = saleRepo.getIdByNumber(number);
		if(id==null) {
			return ResponseEntity.ok().build();
		}else {
			return ResponseEntity.ok().body("Duplicate");
		}
	}
	
	@GetMapping("/sale/update/address")
	ResponseEntity<?> updateBillAddress() {
		List<Sale> sales = saleRepo.findAll();
		List<Customer> customers = customerService.findAll();
		for(Customer customer: customers) {
			if(customer.getBillingAddresses() == null || customer.getBillingAddresses().size() == 0) {
				Address cuAddress = customer.getAddress();
				Address newAddress = new Address();
				newAddress.setCity(cuAddress.getCity());
				newAddress.setDc(customer.getName());
				newAddress.setLabel(cuAddress.getLabel());
				newAddress.setLine(cuAddress.getLine());
				newAddress.setLocationName(cuAddress.getLocationName());
				newAddress.setNotes(cuAddress.getNotes());
				newAddress.setPhone(cuAddress.getPhone());
				newAddress.setState(cuAddress.getState());
				newAddress.setStreet(cuAddress.getStreet());
				newAddress.setType("BIL");
				newAddress.setVisible(false);
				newAddress.setZip(cuAddress.getZip());
				addressRepo.save(newAddress);
				if(customer.getBillingAddresses() == null) {
					customer.setBillingAddresses(new ArrayList<Address>());
				}
				customer.getBillingAddresses().add(newAddress);
				customerService.save(customer);
				log.info("Updating Customer: "+customer.getId());
			}
		}
		for(Sale sale: sales) {
			if(sale.getBillingAddress() == null) {
				sale.setBillingAddress(sale.getCustomer().getBillingAddresses().iterator().next());
				saleRepo.save(sale);
				log.info("Updating Sale: "+sale.getId());
			}
		}
		log.info("Done");
		return ResponseEntity.ok().body("OK");
	}
	
	@PutMapping("/sale/paid")
	HttpEntity<?> setSalesPaid(@RequestBody List<Long> saleIds) {
		log.info("Testing...."+saleIds);
		for(Long saleId: saleIds) {
			Sale sale = saleRepo.getOne(saleId);
			if(sale.isPaidInFull()) {
				continue;
			}
			sale.setPaidInFull(true);
			this.post(sale);
		}
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/sale/{saleId}/duplicate")
	ResponseEntity<?> post(@PathVariable Long saleId) {
		Sale existingSale = saleRepo.getOne(saleId);
		Sale sale = new Sale();
		sale.setCustomer(existingSale.getCustomer());
		sale.setDate(existingSale.getDate());
		sale.setExpectedDate(existingSale.getExpectedDate());
		sale.setFreightTerms(existingSale.getFreightTerms());
		sale.setNotes(existingSale.getNotes());
		sale.setNumber(existingSale.getNumber()+"-Copy");
		sale.setPaymentTerms(existingSale.getPaymentTerms());
		sale.setShippingAddress(existingSale.getShippingAddress());
		sale.setShippingFrom(existingSale.getShippingFrom());
		sale.setShippingTo(existingSale.getShippingTo());
		List<SaleItem> saleItems = new ArrayList<SaleItem>();
		for(SaleItem existingSi: existingSale.getSaleItems()) {
			SaleItem si = new SaleItem();
			si.setItemPackaging(existingSi.getItemPackaging());
			si.setSku(existingSi.getSku());
			si.setUnits(existingSi.getUnits());
			si.setUnitPrice(existingSi.getUnitPrice());
			si.setTotalUnitPrice(existingSi.getTotalUnitPrice());
			si.setSale(sale);
			si.setItemPackaging(existingSi.getItemPackaging());
			saleItems.add(si);
		}
		sale.setSaleItems(saleItems);
		return this.post(sale);
	}
	
	@PostMapping("/sale")
	ResponseEntity<?> post(@RequestBody Sale sale) {
		List<Long> itemIds = new ArrayList<Long>();
		if(sale.getId()!=null) {
			Sale prevSale = saleRepo.getOne(sale.getId());
			for(SaleItem si: prevSale.getSaleItems()) {
				itemIds.add(si.getItemPackaging().getItem().getId());
			}
		}
		if(!sale.getNumber().matches("^[a-zA-Z0-9\\-]{1,25}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale Number is invalid. Alphanumeric and hyphen only allowed. Maximum 25 characters.");
		}
		Long id = saleRepo.getIdByNumber(sale.getNumber());
		if((sale.getId()==null && id !=null) || (sale.getId()!=null && id !=null && !sale.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale Number already exists. Please, choose differrent.");
		}
		for (SaleItem sa : sale.getSaleItems()) {
			sa.setSale(sale);
		}
		sale = (Sale) crudService.merge(sale);
		sale.updateUnits();
		for (SaleItem si : sale.getSaleItems()) {
			itemIds.add(si.getItemPackaging().getItem().getId());
		}
		itemService.updateUnits(itemIds);
		Sale result = (Sale) crudService.save(sale);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/sale/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Sale sale = saleRepo.getOne(id);
//		if(sale.getSaleItems().size()>0) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Sale Items!");
//		}
		List<Long> itemIds = new ArrayList<Long>();
		List<Long> saleIds = new ArrayList<Long>();
		for (SaleItem si : sale.getSaleItems()) {
//			if(si.getShipmentItems().size() > 0) {
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sale has been shipped already. Cannot delete!");
//			}
//			saleItemRepo.deleteById(si.getId());
			itemIds.add(si.getItemPackaging().getItem().getId());
			saleIds.add(si.getSale().getId());
		}
//		sale.setSaleItems(null);
		saleRepo.deleteById(id);
		saleService.updateUnits(saleIds);
		itemService.updateUnits(itemIds);
		return ResponseEntity.ok().build();
	}
	
	private byte[] generateXls(List<Long> saleIds) throws IOException {
		DateTimeFormatter windowFormat = DateTimeFormatter.ofPattern("MM/dd");
		List<Sale> sales = saleRepo.findAllById(saleIds);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Persons");
//		sheet.setColumnWidth(0, 6000);
//		sheet.setColumnWidth(1, 6000);
		 
		Row rowHeader = sheet.createRow(0);	 
		CellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		addCell(0, "ED", rowHeader);
		addCell(1, "PO#", rowHeader);
		addCell(2, "Ship Address", rowHeader);
		addCell(3, "Expected", rowHeader);
		addCell(4, "SKU", rowHeader);
		addCell(5, "Items", rowHeader);
		addCell(6, "Description", rowHeader);
		addCell(7, "C/P", rowHeader);
		addCell(8, "Units", rowHeader);
		addCell(9, "Total", rowHeader);
		addCell(10, "Cases", rowHeader);
		addCell(11, "Pallets", rowHeader);
		addCell(12, "Total Weight", rowHeader);
		addCell(13, "Total Pallets", rowHeader);
		
		XSSFFont cellFont = ((XSSFWorkbook) workbook).createFont();
		cellFont.setFontName("Arial");
		cellFont.setBold(false);
		CellStyle cellStyle = workbook.createCellStyle();
		int count = 1;
		int saleCount = 0;
		for(Sale sale: sales) {
			saleCount++;
			if(sale.getSaleItems().size()==0) {
				Row row = sheet.createRow(count);
				cellStyle.setFont(cellFont);
				addCell(0, String.valueOf(saleCount), row);
				addCell(1, sale.getNumber(), row);
				Address address = sale.getShippingAddress();
				String addressLabel = "";
				if(sale.getShippingAddress()!=null) {
					addressLabel = address.getDc()+", "+address.getStreet()+", "+address.getCity()+", "+address.getState()+", "+address.getZip();
				}
				addCell(2, addressLabel, row);
				String windowDate = "";
				if(sale.getShippingFrom()!=null) {
					windowDate += sale.getShippingFrom().format(windowFormat)+"-";
				}
				if(sale.getShippingTo()!=null) {
					windowDate += sale.getShippingTo().format(windowFormat);
				}
				addCell(3, windowDate, row);
				count++;
			}
			long totalPallets = 0;
			for(SaleItem si: sale.getSaleItems()) {
				long units = si.getUnits() + si.getUnitsAdjusted();
				long cases = (long) Math.ceil((double) units/si.getItemPackaging().getPackaging().getCasePack());
				long pallets = (long) Math.ceil((double) cases/(si.getItemPackaging().getPackaging().getHi()*si.getItemPackaging().getPackaging().getTi()));
				totalPallets += pallets;
			}
			for(SaleItem si: sale.getSaleItems()) {
				Row row = sheet.createRow(count);
				cellStyle.setFont(cellFont);
				addCell(0, String.valueOf(saleCount), row);
				addCell(1, sale.getNumber(), row);
				Address address = sale.getShippingAddress();
				String addressLabel = "";
				if(sale.getShippingAddress()!=null) {
					addressLabel = address.getDc()+", "+address.getStreet()+", "+address.getCity()+", "+address.getState()+", "+address.getZip();
				}
				addCell(2, addressLabel, row);
				String windowDate = "";
				if(sale.getShippingFrom()!=null) {
					windowDate += sale.getShippingFrom().format(windowFormat)+"-";
				}
				if(sale.getShippingTo()!=null) {
					windowDate += sale.getShippingTo().format(windowFormat);
				}
				addCell(3, windowDate, row);
				addCell(3, sale.getDate()==null?"":sale.getDate().format(windowFormat), row);
				addCell(4, si.getSku(), row);
				addCell(5, si.getItemPackaging().getItem().getNumber(), row);
				addCell(6, si.getItemPackaging().getItem().getName(), row);
				addCell(7, String.valueOf(si.getItemPackaging().getPackaging().getCasePack()), row);
				addCell(8, String.valueOf(si.getUnits()), row);
				addCell(9, String.valueOf(si.getTotalUnitPrice()), row);
				int cases = BigDecimal.valueOf(si.getUnits()).divide(BigDecimal.valueOf(si.getItemPackaging().getPackaging().getCasePack()),RoundingMode.CEILING).intValue();
				cases = cases==0?1:cases;
				int pallets = (si.getItemPackaging().getPackaging().getTi()*si.getItemPackaging().getPackaging().getHi())/cases;
				pallets = pallets==0?1:pallets;
				BigDecimal unitsWeight = si.getItemPackaging().getItem().getWeight().multiply(BigDecimal.valueOf(si.getUnits()));
				BigDecimal palletsWeight = si.getItemPackaging().getPackaging().getPalletWeight().multiply(BigDecimal.valueOf(pallets));
				BigDecimal caseWeight = si.getItemPackaging().getPackaging().getCaseWeight().multiply(BigDecimal.valueOf(cases));
				int totalWeight = unitsWeight.add(palletsWeight).add(caseWeight).intValue();
				addCell(10, String.valueOf(cases), row);
				addCell(11, String.valueOf(pallets), row);
				addCell(12, String.valueOf(totalWeight), row);
				addCell(13, String.valueOf(totalPallets), row);
				count++;
			}
			int firstRow = count-(sale.getSaleItems().size()==0?1:sale.getSaleItems().size());
			int lastRow = count-1;
			if(firstRow!=lastRow) {
				sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, 0,0));
				sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, 1,1));
				sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, 2,2));
				sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, 3,3));
			}
		}
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