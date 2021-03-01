package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.dto.PackagingListDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Packaging;
import com.noovitec.mpb.repo.PackagingRepo;
import com.noovitec.mpb.service.AttachmentService;

@RestController
@RequestMapping("/api")
class PackagingRest {

	private final Logger log = LoggerFactory.getLogger(PackagingRest.class);
	private PackagingRepo packagingRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AttachmentService attachmentService;
	
	public PackagingRest(PackagingRepo packagingRepo) {
		this.packagingRepo = packagingRepo;
	}

	@GetMapping("/packaging/kv")
	Collection<KeyValueDto> getAllKv(
			@RequestParam(required = false) Long itemId) {
		if(itemId == null) {
			return packagingRepo.getAllKv();
		} else {
			return packagingRepo.findKvsByItem(itemId);
		}
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
			dto.setCaseLength(p.getCaseLength());
			dto.setCaseWidth(p.getCaseWidth());
			dto.setCasePack(p.getCasePack());
			dto.setCaseWeight(p.getCaseWeight());
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
	ResponseEntity<?> post(@RequestParam(required = false) MultipartFile image, @RequestParam String jsonPackaging) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		Packaging packaging = objectMapper.readValue(jsonPackaging, Packaging.class);
		packaging = packagingRepo.save(packaging);
		if(image!=null) {
			Attachment attachment = attachmentService.store(image, Packaging.class.getSimpleName(), packaging.getId(), packaging.getAttachment());
			packaging.setAttachment(attachment);
			packaging = packagingRepo.save(packaging);
		}
		return ResponseEntity.ok().body(packaging);
	}

	@DeleteMapping("/packaging/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		packagingRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}