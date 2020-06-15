package com.noovitec.mpb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.InvoiceItem;
import com.noovitec.mpb.repo.custom.CustomInvoiceItemRepo;

public interface InvoiceItemRepo extends PagingAndSortingRepository<InvoiceItem, Long>, CustomInvoiceItemRepo {
	
}