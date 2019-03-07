package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.SaleDto;
import com.noovitec.mpb.entity.Address;
import com.noovitec.mpb.entity.Sale;
import com.noovitec.mpb.entity.SaleItem;
import com.noovitec.mpb.repo.AddressRepo;
import com.noovitec.mpb.repo.SaleRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class SaleRest {

	private final Logger log = LoggerFactory.getLogger(SaleRest.class);
	private SaleRepo saleRepo;

	public SaleRest(SaleRepo saleRepo) {
		this.saleRepo = saleRepo;
	}

	@GetMapping("/sale")
	Collection<Sale> getAll() {
		return saleRepo.findAll();
	}

	@GetMapping("/sale/{id}")
	ResponseEntity<Sale> get(@PathVariable Long id) {
		Optional<Sale> result = saleRepo.findById(id);
		return result.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/sale/purchase/{purchase_id}")
	Collection<SaleDto> getAllByPurchase(@PathVariable Long purchase_id) {
		List<SaleDto> sales = saleRepo.findAllByPurchase(purchase_id);
		return sales;
	}

	@PostMapping("/sale")
	ResponseEntity<Sale> post(@RequestBody(required = false) Sale sale) throws URISyntaxException {
		if (sale == null) {
			sale = new Sale();
		}
		//Needed for update.
		for(SaleItem sa : sale.getSaleItems()) {
			sa.setSale(sale);
		}
		Sale result = saleRepo.save(sale);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/sale/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
//		Optional<Sale> sale = saleRepo.findById(id);
//		sale.get().getCustomer().getAddresses().remove(sale.get());
//		saleRepo.delete(sale.get());
		saleRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}