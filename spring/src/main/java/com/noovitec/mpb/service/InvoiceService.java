package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.repo.InvoiceRepo;

public interface InvoiceService {

	public Invoice save(Invoice invoice);
	public void delete(Long id);
	
	@Transactional
	@Service("invoiceServiceImpl")
	public class InvoiceServiceImp implements InvoiceService {

		private final Logger log = LoggerFactory.getLogger(InvoiceServiceImp.class);
		private InvoiceRepo invoiceRepo;

		public InvoiceServiceImp(InvoiceRepo invoiceRepo) {
			this.invoiceRepo = invoiceRepo;
		}
		
		public Invoice save(Invoice invoice) {
			for (InvoiceItem ii : invoice.getInvoiceItems()) {
				ii.setInvoice(invoice);
			}
			return invoiceRepo.save(invoice);
		}
		
		public void delete(Long id) {
			invoiceRepo.deleteById(id);
		}

	}
}
