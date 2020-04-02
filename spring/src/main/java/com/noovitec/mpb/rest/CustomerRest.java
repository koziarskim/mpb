package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.CustomerDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.repo.CustomerRepo;


@RestController
@RequestMapping("/api")
class CustomerRest {

	private final Logger log = LoggerFactory.getLogger(CustomerRest.class);
	private CustomerRepo customerRepo;

	public CustomerRest(CustomerRepo customerRepo) {
		this.customerRepo = customerRepo;
	}

	@GetMapping("/customer")
	Collection<Customer> getAll() {
		return customerRepo.findAll();
	}

	@GetMapping("/customer/kv")
	Collection<KeyValueDto> getAllCustomers() {
		return customerRepo.findAllCustomers();
	}

	@GetMapping("/customer/kv/item/{itemId}")
	Collection<KeyValueDto> findByItem(@PathVariable Long itemId) {
		return customerRepo.findByItem(itemId);
	}

	@GetMapping("/customer/{id}")
	ResponseEntity<Customer> get(@PathVariable Long id) {
		Optional<Customer> result = customerRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/customer/pageable")
	Page<CustomerDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, 
			@RequestParam(name = "searchKey", required = false) String searchKey,
			@RequestParam(name = "searchType", required = false) String searchType) {
		Page<Customer> customers = null;
		if(searchType==null || searchType.isBlank() || searchKey==null || searchKey.isBlank()) {
			customers = customerRepo.findPage(pageable);
		}else if(searchType.equals("customer") && !searchKey.isBlank()) {
			customers = customerRepo.findPageByCustomer(pageable, searchKey);
		}
		if(customers == null) {
			 return Page.empty();
		}
		Page<CustomerDto> dtos = customers.map(customer -> {
			CustomerDto dto = new CustomerDto();
			dto.setId(customer.getId());
			dto.setAccount(customer.getAccount());
			dto.setName(customer.getName());
			dto.setPhone(customer.getPhone());
			dto.setAddressName(customer.getBillingAddress()==null?"":(customer.getBillingAddress().getCity()+", "+customer.getBillingAddress().getState()));
			dto.setUnitsSold(customer.getUnitsSold());
			dto.setUnitsShipped(customer.getUnitsShipped());
			return dto;
		});
		return dtos;
	}

	@PostMapping("/customer")
	ResponseEntity<?> post(@RequestBody(required = false) Customer customer) throws URISyntaxException {
		if (customer == null) {
			customer = new Customer();
		}
//		if(customer.getInvoiceType()==null) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invoice Type is required");
//		}
		Customer result = customerRepo.save(customer);
		result.setAccount(result.getId().toString());
		result = customerRepo.save(customer);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/customer/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		customerRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}