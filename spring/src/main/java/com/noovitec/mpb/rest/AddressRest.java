package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Address;
import com.noovitec.mpb.repo.AddressRepo;


@RestController
@RequestMapping("/api")
class AddressRest {

	private final Logger log = LoggerFactory.getLogger(AddressRest.class);
	private AddressRepo addressRepo;

	public AddressRest(AddressRepo addressRepo) {
		this.addressRepo = addressRepo;
	}

	@GetMapping("/address")
	Collection<Address> getAll() {
		return addressRepo.findAll();
	}

	@GetMapping("/address/{id}")
	ResponseEntity<Address> get(@PathVariable Long id) {
		Optional<Address> result = addressRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/address/customer/{customer_id}")
	Collection<KeyValueDto> getByCustomer(@PathVariable Long customer_id) {
		List<KeyValueDto> dtos = new ArrayList<KeyValueDto>();
		List<Address> addresses = addressRepo.findByCustomer(customer_id);
		for(Address a: addresses) {
			KeyValueDto dto = new KeyValueDto();
			dto.setId(a.getId());
			dto.setName(a.getDc() + " - "+ a.getStreet());
			dtos.add(dto);
		}
		return dtos;
	}
	
	@GetMapping("/address/type/{type}")
	Collection<KeyValueDto> getByType(@PathVariable String type) {
		if(type.equalsIgnoreCase("FRG")) {
			return addressRepo.findFreightKv();
		}
		return addressRepo.findKvByType(type);
	}

	@PostMapping("/address")
	ResponseEntity<Address> post(@RequestBody(required = false) Address address) throws URISyntaxException {
		if (address == null) {
			address = new Address();
		}
		Address result = addressRepo.save(address);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/address/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<Address> address = addressRepo.findById(id);
		addressRepo.delete(address.get());
		return ResponseEntity.ok().build();
	}
}