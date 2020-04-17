package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.Invoice;
import com.noovitec.mpb.repo.custom.CustomInvoiceRepo;

public interface InvoiceRepo extends PagingAndSortingRepository<Invoice, Long>, CustomInvoiceRepo {
	
	@Query("select inv.id from Invoice inv where inv.number = :invoiceNumber")
	Long getIdByNumber(String invoiceNumber);

	@Query("select inv from Invoice inv "
			+ "join inv.shipment ship "
			+ "join ship.customer cu "
			+ "where cu.id = :customerId")
	Invoice getIdByCustomer(Long customerId);

}