package com.noovitec.mpb.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.InvoiceRepo;
import com.noovitec.mpb.repo.SaleItemRepo;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.repo.ShipmentRepo;

public interface InvoiceService {

	public List<Invoice> createInvoiceForShipment(Shipment shipment);
	public Invoice createInvoiceForSale(Sale sale);
	public Invoice save(Invoice invoice);
	public void delete(Long id);
	public byte[] generatePdf(Invoice invoice, boolean submitted) throws IOException, DocumentException;
	
	@Transactional
	@Service("invoiceServiceImpl")
	public class InvoiceServiceImp implements InvoiceService {

		private final Logger log = LoggerFactory.getLogger(InvoiceServiceImp.class);
		private InvoiceRepo invoiceRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		SaleRepo saleRepo;
		@Autowired
		ShipmentRepo shipmentRepo;
		@Autowired
		SaleItemRepo saleItemRepo;

		public InvoiceServiceImp(InvoiceRepo invoiceRepo) {
			this.invoiceRepo = invoiceRepo;
		}

		public Invoice createInvoiceForSale(Sale sale) {
			Invoice invoice = null;
			Customer customer = sale.getCustomer();
			if(customer == null) {
				return null;
			}
			String type = customer.getInvoiceType();
			Shipment shipment = null;
			if(Customer.INVOICE_TYPE.PER_FIRST_SHIPMENT.name().equalsIgnoreCase(type)) {
				shipment = shipmentRepo.getFirstBySale(sale.getId());
			}
			if(Customer.INVOICE_TYPE.PER_LAST_SHIPMENT.name().equalsIgnoreCase(type)) {
				shipment = shipmentRepo.getLastBySale(sale.getId());
			}
			if(shipment == null) {
				return null;
			}
			invoiceRepo.deleteByShipment(shipment.getId());
			invoice = new Invoice();
			invoice.setType(type);
			invoice.setShipment(shipment);
			invoice.setBillingAddress(customer.getBillingAddress());
			invoice.setShippingAddress(shipment.getShippingAddress());
			invoice.setDate(LocalDate.now());
			invoice.setFob(shipment.getFob());
			invoice.setVia(shipment.getVia());
			invoice.setLoadNumber(shipment.getLoadNumber());
			invoice.setPaymentTerms(customer.getPaymentTerms());
			for(SaleItem saleItem: sale.getSaleItems()) {
				InvoiceItem ii = new InvoiceItem();
				ii.setSaleItem(saleItem);
				ii.setUnitPrice(saleItem.getUnitPrice());
				ii.setUnitsInvoiced(Long.valueOf(saleItem.getUnits()));
				ii.setTotalUnitPrice(ii.getUnitPrice().multiply(BigDecimal.valueOf(ii.getUnitsInvoiced())));
				invoice.getInvoiceItems().add(ii);
			}
			invoice = this.save(invoice);
			invoice.setNumber(sale.getNumber());
			invoice = this.save(invoice);
			return invoice;
		}
		
		public List<Invoice> createInvoiceForShipment(Shipment shipment) {
			invoiceRepo.deleteByShipment(shipment.getId());
			List<Invoice> invoices = new ArrayList<Invoice>();
			Customer customer = shipment.getCustomer();
			if(customer == null) {
				return null;
			}
			if(customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_SHIPMENT_ITEM.name())) {
				Invoice invoice = new Invoice();
				invoice.setType(Customer.INVOICE_TYPE.PER_SHIPMENT_ITEM.name());
				invoice.setShipment(shipment);
				invoice.setBillingAddress(customer.getBillingAddress());
				invoice.setShippingAddress(shipment.getShippingAddress());
				invoice.setDate(LocalDate.now());
				invoice.setFob(shipment.getFob());
				invoice.setVia(shipment.getVia());
				invoice.setLoadNumber(shipment.getLoadNumber());
				invoice.setPaymentTerms(customer.getPaymentTerms());
				for(ShipmentItem shipItem: shipment.getShipmentItems()) {
					InvoiceItem ii = new InvoiceItem();
					ii.setSaleItem(shipItem.getSaleItem());
					ii.setUnitPrice(shipItem.getSaleItem().getUnitPrice());
					ii.setUnitsInvoiced(shipItem.getUnits());
					ii.setTotalUnitPrice(ii.getUnitPrice().multiply(BigDecimal.valueOf(ii.getUnitsInvoiced())));
					invoice.getInvoiceItems().add(ii);
				}
				invoice = this.save(invoice);
				invoice.setNumber(invoice.getId().toString());
				invoice = this.save(invoice);
				invoices.add(invoice);
			}
			
			if(customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_SHIPMENT_SALE.name())) {
				Map<Long, List<ShipmentItem>> saleMap = new HashMap<Long, List<ShipmentItem>>();
				for(ShipmentItem shipItem: shipment.getShipmentItems()) {
					List<ShipmentItem> list = saleMap.get(shipItem.getSaleItem().getSale().getId());
					if(list==null) {
						list = new ArrayList<ShipmentItem>();
						saleMap.put(shipItem.getSaleItem().getSale().getId(), list);
					}
					list.add(shipItem);
				}
				for (Map.Entry<Long, List<ShipmentItem>> entry : saleMap.entrySet()) {
					List<ShipmentItem> shipItems = entry.getValue();
					Invoice invoice = new Invoice();
					invoice.setType(Customer.INVOICE_TYPE.PER_SHIPMENT_SALE.name());
					invoice.setShipment(shipment);
					invoice.setBillingAddress(customer.getBillingAddress());
					invoice.setShippingAddress(shipment.getShippingAddress());
					invoice.setDate(LocalDate.now());
					invoice.setFob(shipment.getFob());
					invoice.setVia(shipment.getVia());
					invoice.setLoadNumber(shipment.getLoadNumber());
					invoice.setPaymentTerms(customer.getPaymentTerms());
					for(ShipmentItem shipItem: shipItems) {
						InvoiceItem ii = new InvoiceItem();
						ii.setSaleItem(shipItem.getSaleItem());
						ii.setUnitPrice(shipItem.getSaleItem().getUnitPrice());
						ii.setUnitsInvoiced(shipItem.getUnits());
						invoice.getInvoiceItems().add(ii);
					}
					invoice = this.save(invoice);
					invoice.setNumber(invoice.getId().toString());
					invoice = this.save(invoice);
					invoices.add(invoice);
				}
			}
			return invoices;
		}
		
		public Invoice save(Invoice invoice) {
			for (InvoiceItem ii : invoice.getInvoiceItems()) {
				ii.setInvoice(invoice);
			}
			invoice = (Invoice) crudService.merge(invoice);
			return invoiceRepo.save(invoice);
		}
		
		public void delete(Long id) {
			invoiceRepo.deleteById(id);
		}
		
		public byte[] generatePdf(Invoice invoice, boolean submitted) throws IOException, DocumentException {
			String itemSaleNumber = "";
			String itemQuantity = "";
			String itemDescription = "";
			String itemCasePack = "";
			String itemPrice = "";
			String itemTotalPrice = "";
			int totalUnits = 0;
			BigDecimal totalAmount = BigDecimal.ZERO;
			Long totalCases = 0L;
			Long totalPallets = 0L;
			Collection<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
			Map<Long, String> saleIds = new HashMap<Long, String>();
			int count = 0;
			for (InvoiceItem ii : invoiceItems) {
					saleIds.put(ii.getSaleItem().getSale().getId(), ii.getSaleItem().getSale().getNumber());
					itemSaleNumber += ii.getSaleItem().getSale().getNumber() +"\n\n";
					itemQuantity += ii.getUnitsInvoiced() + "\n\n";
					itemDescription += ii.getSaleItem().getItem().getNumber() + " - " +ii.getSaleItem().getItem().getName()+"\n" 
							+(ii.getSaleItem().getItem().getUpc()==null?"":"UPC: "+ii.getSaleItem().getItem().getUpc())+(ii.getSaleItem().getSku()==null?"":", SKU# "+ ii.getSaleItem().getSku()) + "\n";
					itemCasePack += ii.getSaleItem().getItem().getCasePack() + "\n\n";
					itemPrice += ii.getUnitPrice() + "\n\n";
					totalUnits += ii.getUnitsInvoiced();
					BigDecimal itemTotalPriceBd = ii.getUnitPrice().multiply(new BigDecimal(ii.getUnitsInvoiced())).setScale(2, RoundingMode.CEILING);
					itemTotalPrice += itemTotalPriceBd.toString()  + "\n\n";
					totalAmount = totalAmount.add(itemTotalPriceBd==null?BigDecimal.ZERO:itemTotalPriceBd);
					totalCases += ii.getUnitsInvoiced()/ii.getSaleItem().getItem().getCasePack();
					totalPallets += ii.getUnitsInvoiced()/(ii.getSaleItem().getItem().getTi() * ii.getSaleItem().getItem().getHi());
			}
			totalAmount = totalAmount.add(invoice.getShippingCost()==null?BigDecimal.ZERO:invoice.getShippingCost());
			InputStream bolIn = null;
			bolIn = this.getClass().getClassLoader().getResourceAsStream("pdf/Invoice-Template-1.pdf");
			PdfReader mainReader = new PdfReader(bolIn);
			ByteArrayOutputStream bolBaos = new ByteArrayOutputStream();
			PdfStamper bolStamper = new PdfStamper(mainReader, bolBaos);
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
				String billingAddress = customer.getName() + "\n" 
					+ customer.getBillingAddress().getStreet() + "\n" 
					+ customer.getBillingAddress().getCity() + ", "+ customer.getBillingAddress().getState() + " "+customer.getBillingAddress().getZip();		
				bolStamper.getAcroFields().setField("billingAddress", billingAddress);
			}
			if(shipment.getShippingAddress()!=null) {
				String phone = null;//shipment.getShippingAddress().getPhone()==null?shipment.getCustomer().getPhone():shipment.getShippingAddress().getPhone();
				String shippingAddress = shipment.getCustomer().getName() + " - "+shipment.getShippingAddress().getDc() + "\n"
					+ (shipment.getShippingAddress().getLine()==null?"":(shipment.getShippingAddress().getLine() + "\n"))	
					+ shipment.getShippingAddress().getStreet() + "\n" 
					+ shipment.getShippingAddress().getCity() + ", " + shipment.getShippingAddress().getState() + " "+shipment.getShippingAddress().getZip() + "\n"
					+ (phone==null?"":("Phone: "+phone + "\n"))
					+ (shipment.getShippingAddress().getNotes()==null?"":shipment.getShippingAddress().getNotes());
				bolStamper.getAcroFields().setField("shippingAddress", shippingAddress);
			}
			bolStamper.getAcroFields().setField("paymentTerms",  invoice.getPaymentTerms());
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
			bolStamper.getAcroFields().setField("totalCases", totalCases.toString());
			bolStamper.getAcroFields().setField("totalPallets", totalPallets.toString());
			bolStamper.getAcroFields().setField("balanceDue", invoice.getBalanceDue()==null?"0.00":invoice.getBalanceDue().toString());
			bolStamper.getAcroFields().setField("shippingCost", invoice.getShippingCost()==null?"0.00":invoice.getShippingCost().toString());
			bolStamper.getAcroFields().setField("totalAmount", totalAmount.toString());
//			List<String> iss = Arrays.asList("pdf/Invoice-Template-2.pdf","pdf/Invoice-Template-2.pdf","pdf/Invoice-Template-2.pdf");
//			this.concatenatePdfs(iss, bolBaos);
			bolStamper.close();
			mainReader.close();
			return bolBaos.toByteArray();
		}
		
		private void concatenatePdfs(List<String> listOfPdfFiles, ByteArrayOutputStream outputStream) throws DocumentException, IOException {
			Document document = new Document();
	        PdfCopy copy = new PdfSmartCopy(document, outputStream);
	        document.open();
	        int count = 0;
	        for (String newPage : listOfPdfFiles) {
	        	count++;
	        	InputStream is = this.getClass().getClassLoader().getResourceAsStream(newPage);
	            PdfReader reader = new PdfReader(is);
	            ByteArrayOutputStream newIs = new ByteArrayOutputStream();
	            PdfStamper stamper = new PdfStamper(reader, newIs);
	            AcroFields form = stamper.getAcroFields();
	            form.setField("saleNumber", "122344-"+count);
	            stamper.setFormFlattening(true);
	            stamper.close();
	            reader.close();
	            copy.addDocument(reader);
	        }
	        document.close();
	}

	}
}
