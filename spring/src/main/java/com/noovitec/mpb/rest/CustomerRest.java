package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.repo.CustomerRepo;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.NotificationService;


@RestController
@RequestMapping("/api")
class CustomerRest {

	private final Logger log = LoggerFactory.getLogger(CustomerRest.class);
	private CustomerRepo customerRepo;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private CrudService crudService;

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
	Page<CustomerDto> getAllPageable(@RequestParam(required = false) Pageable pageable, 
			@RequestParam(required = false) String customerName) {
		Page<Customer> customers = null;
		if (customerName == null || customerName.trim().length() == 0) {
			customers = customerRepo.findPage(pageable);
		}else {
			customers = customerRepo.findPageByCustomer(pageable, customerName);
		}
		if(customers == null) {
			 return Page.empty();
		}
		Page<CustomerDto> dtos = customers.map(customer -> {
			CustomerDto dto = new CustomerDto();
			dto.setId(customer.getId());
			dto.setAccount(customer.getAccount());
			dto.setName(customer.getName());
			dto.setAddressName(customer.getAddress()==null?"":(customer.getAddress().getCity()+", "+customer.getAddress().getState()));
			return dto;
		});
		return dtos;
	}

	@PostMapping("/customer")
	ResponseEntity<?> post(@RequestBody(required = false) Customer customer) throws URISyntaxException {
		if (customer == null) {
			customer = new Customer();
		}
		Long customerId = customer.getId();
		customer = (Customer) crudService.merge(customer);
		if(customerId == null) {
			List<String> emails = new ArrayList<String>();
			emails.add("kzygulska@marketplacebrands.com");
			emails.add("vtomasik@marketplacebrands.com");
			Map<String, String> model = new HashMap<String, String>();
			model.put("customerName", customer.getName());
			notificationService.sendMail(emails, model, Notification.TYPE.CUSTOMER_CREATED);
		}
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