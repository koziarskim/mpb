package com.noovitec.mpb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.InvoiceRepo;
import com.noovitec.mpb.repo.SaleRepo;
import com.noovitec.mpb.repo.ShipmentRepo;

public interface InvoiceService {

	public List<Invoice> createInvoiceForShipment(Shipment shipment);
	public Invoice createInvoiceForSale(Sale sale);
	public Invoice save(Invoice invoice);
	public void delete(Long id);
	
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

		public InvoiceServiceImp(InvoiceRepo invoiceRepo) {
			this.invoiceRepo = invoiceRepo;
		}

		public Invoice createInvoiceForSale(Sale sale) {
			log.info("1 Creating ivoice Sale: "+sale.getId());
			Invoice invoice = null;
			Customer customer = sale.getCustomer();
			if(customer !=null && customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_FIRST_SHIPMENT.name())) {
				log.info("2 Creating ivoice Sale: "+sale.getId());
				Shipment shipment = shipmentRepo.getFirstBySale(sale.getId());
				invoice = new Invoice();
				invoice.setType(Customer.INVOICE_TYPE.PER_FIRST_SHIPMENT.name());
				invoice.setShipment(shipment);
				invoice.setNumber(shipment.getNumber()+"-"+new Random().nextInt(1000));
				for(SaleItem saleItem: sale.getSaleItems()) {
					InvoiceItem ii = new InvoiceItem();
					ii.setSaleItem(saleItem);
					ii.setUnitPrice(saleItem.getUnitPrice());
					ii.setUnitsInvoiced(Long.valueOf(saleItem.getUnits()));
					invoice.getInvoiceItems().add(ii);
				}
				invoice = this.save(invoice);
			}
			return invoice;
		}
		
		public List<Invoice> createInvoiceForShipment(Shipment shipment) {
			List<Invoice> invoices = new ArrayList<Invoice>();
			Customer customer = shipment.getCustomer();

			if(customer.getInvoiceType().equalsIgnoreCase(Customer.INVOICE_TYPE.PER_SHIPMENT_ITEM.name())) {
				Invoice invoice = new Invoice();
				invoice.setType(Customer.INVOICE_TYPE.PER_SHIPMENT_ITEM.name());
				invoice.setShipment(shipment);
				invoice.setNumber(shipment.getNumber()+"-1");
				for(ShipmentItem shipItem: shipment.getShipmentItems()) {
					InvoiceItem ii = new InvoiceItem();
					ii.setSaleItem(shipItem.getSaleItem());
					ii.setUnitPrice(shipItem.getSaleItem().getUnitPrice());
					ii.setUnitsInvoiced(shipItem.getUnits());
					invoice.getInvoiceItems().add(ii);
				}
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
					invoice.setNumber(shipment.getNumber()+"-"+entry.getKey());
					for(ShipmentItem shipItem: shipItems) {
						InvoiceItem ii = new InvoiceItem();
						ii.setSaleItem(shipItem.getSaleItem());
						ii.setUnitPrice(shipItem.getSaleItem().getUnitPrice());
						ii.setUnitsInvoiced(shipItem.getUnits());
						invoice.getInvoiceItems().add(ii);
					}
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

	}
}
