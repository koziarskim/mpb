package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.SupplierDto;
import com.noovitec.mpb.entity.Supplier;
import com.noovitec.mpb.repo.SupplierRepo;


@RestController
@RequestMapping("/api")
class SupplierRest {

	private final Logger log = LoggerFactory.getLogger(SupplierRest.class);
	private SupplierRepo supplierRepo;

	public SupplierRest(SupplierRepo supplierRepo) {
		this.supplierRepo = supplierRepo;
	}

	@GetMapping("/supplier")
	Collection<Supplier> getAll() {
		return (Collection<Supplier>) supplierRepo.findAll();
	}

	@GetMapping("/supplier/pageable")
	Page<Supplier> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		Page<Supplier> all = null;
		if(searchKey ==null || searchKey.trim().length() == 0) {
			all = supplierRepo.findAll(pageable);
		}else {
			all = supplierRepo.findAll(pageable, searchKey);
		}
		return all;
	}

	@GetMapping("/supplier/{id}")
	ResponseEntity<Supplier> get(@PathVariable Long id) {
		Optional<Supplier> result = supplierRepo.findById(id);
		return ResponseEntity.ok().body(result.get());
	}

	//Get all suppliers included in purchase (components associated with sales for purchase).
	@GetMapping("/supplier/purchase/{purchase_id}")
	Collection<SupplierDto> findAllSuppliersForPurchase(@PathVariable Long purchase_id) {
		Collection<SupplierDto> result = supplierRepo.findAllSuppliersForPurchase(purchase_id);
		return result;
	}

	@PostMapping("/supplier")
	ResponseEntity<Supplier> post(@RequestBody(required = false) Supplier supplier) throws URISyntaxException {
		if (supplier == null) {
			supplier = new Supplier();
		}
		Supplier result = supplierRepo.save(supplier);
		result.setAccount(result.getId().toString());
		result = supplierRepo.save(supplier);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/supplier/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		supplierRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}