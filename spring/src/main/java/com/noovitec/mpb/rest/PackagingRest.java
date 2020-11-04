package com.noovitec.mpb.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.PackagingListDto;
import com.noovitec.mpb.entity.Packaging;
import com.noovitec.mpb.repo.PackagingRepo;

@RestController
@RequestMapping("/api")
class PackagingRest {

	private final Logger log = LoggerFactory.getLogger(PackagingRest.class);
	private PackagingRepo packagingRepo;
	
	public PackagingRest(PackagingRepo packagingRepo) {
		this.packagingRepo = packagingRepo;
	}

	@GetMapping("/packaging")
	ResponseEntity<?> getAll() {
		List<Packaging> packagings = packagingRepo.findAll();
		List<PackagingListDto> dtos = new ArrayList<PackagingListDto>();
		for(Packaging p: packagings) {
			PackagingListDto dto = new PackagingListDto();
			dto.setId(p.getId());
			dto.setName(p.getName());
			dto.setTypeLabel(Packaging.TYPE.valueOf(p.getType()).label());
			dto.setCaseHeight(p.getCaseHeight());
			dto.setCaseWidth(p.getCaseWidth());
			dto.setCaseDepth(p.getCaseDepth());
			dto.setCasePack(p.getCasePack());
			dto.setHi(p.getHi());
			dto.setTi(p.getTi());
			dto.setPalletWeight(p.getPalletWeight());
			dto.setWarehouseCost(p.getWarehouseCost());
			dto.setPackageCost(p.getPackageCost());
			dtos.add(dto);
		}
		return ResponseEntity.ok().body(dtos);
	}

	@GetMapping("/packaging/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Packaging> packaging = packagingRepo.findById(id);
		return ResponseEntity.ok().body(packaging);
	}
	
	@PostMapping("/packaging")
	ResponseEntity<?> post(@RequestBody Packaging packaging) {
		packaging = packagingRepo.save(packaging);
		return ResponseEntity.ok().body(packaging);
	}

	@DeleteMapping("/packaging/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		packagingRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}