package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noovitec.mpb.dto.ComponentDto;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Category;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.CategoryRepo;
import com.noovitec.mpb.repo.ComponentRepo;

@RestController
@RequestMapping("/api")
class ComponentRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;
	@Autowired
	CategoryRepo categoryRepo;

	private final Logger log = LoggerFactory.getLogger(ComponentRest.class);
	private ComponentRepo componentRepo;

	public ComponentRest(ComponentRepo componentRepo) {
		this.componentRepo = componentRepo;
	}

	@GetMapping("/component")
	Iterable<Component> getAll() {
		return componentRepo.findAll();
	}

	@GetMapping("/component/pageable")
	Page<ComponentDto> getAllPageable(@RequestParam(name = "pageable", required = false) Pageable pageable, @RequestParam(name = "nameSearch", required = false) String nameSearch) {
		Page<Component> components = null;
		if(nameSearch ==null || nameSearch.trim().length() == 0) {
			components = componentRepo.findAll(pageable);
		}else {
			components = componentRepo.findAll(pageable, nameSearch);
		}
		if(components == null) {
			 return Page.empty();
		}
		Page<ComponentDto> dtos = components.map(component -> {
			ComponentDto dto = new ComponentDto();
			dto.setId(component.getId());
			dto.setNumber(component.getNumber());
			dto.setName(component.getName());
			dto.setCategoryName(component.getCategory().getName());
			dto.setSupplierName(component.getSupplier().getName());
			dto.setUnitsOnStock(component.getUnitsOnStock());
		    return dto;
		});
		return dtos;
	}

	@GetMapping("/component/kv")
	Collection<KeyValueDto> getAllKeyValue(@RequestParam(name = "purchase_id", required = false) Long purchase_id) {
		List<KeyValueDto> dtos = null;
		if(purchase_id ==null) {
			dtos = componentRepo.getAllKeyValue();
		}else {
			dtos = componentRepo.findKvByPurchase(purchase_id);
		}
		return dtos;
	}

	@GetMapping("/component/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		Optional<Component> component = componentRepo.findById(id);
		return component.map(response -> ResponseEntity.ok().body(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/component/number/category/{category_id}")
	ResponseEntity<?> getAvailableNumberByCategory(@PathVariable Long category_id) {
		Category category = categoryRepo.findById(category_id).get();
		String prefix = category.getPrefix();
		Component component = componentRepo.getLast();
		Long component_id = (component == null ? 0L : component.getId()) + 1;
		String number = prefix + component_id.toString();
		return ResponseEntity.ok().body(Collections.singletonMap("number", number));
	}

	@PostMapping("/component")
	ResponseEntity<Component> post(@RequestBody(required = false) Component component) throws URISyntaxException {
		if (component == null) {
			component = new Component();
		}
		Component result = componentRepo.save(component);
		return ResponseEntity.ok().body(result);
	}

	// This includes image upload.
	@PostMapping("/component/upload")
	ResponseEntity<Component> postComponentAndAttachment(@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam("jsonComponent") String jsonComponent) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		Component component = objectMapper.readValue(jsonComponent, Component.class);
		if (image != null) {
			Attachment attachment = new Attachment();
			attachment.setData(image.getBytes());
			attachmentRepo.save(attachment);
			component.setAttachment(attachment);
		}
		Component result = componentRepo.save(component);
		return ResponseEntity.created(new URI("/api/component/" + result.getId())).body(result);
	}

	@DeleteMapping("/component/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Component component = componentRepo.findById(id).get();
		if (component.isLocked()) {
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Collections.singletonMap("message", "Component is currently locked"));
		}
		componentRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}