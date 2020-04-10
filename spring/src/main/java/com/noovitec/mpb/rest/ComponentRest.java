package com.noovitec.mpb.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.noovitec.mpb.service.AttachmentService;
import com.noovitec.mpb.service.ComponentService;
import com.noovitec.mpb.service.CrudService;
import com.noovitec.mpb.service.ItemService;

@RestController
@RequestMapping("/api")
class ComponentRest {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AttachmentRepo attachmentRepo;
	@Autowired
	private CategoryRepo categoryRepo;
	@Autowired
	private CrudService crudService;
	@Autowired
	private ComponentService componentService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private AttachmentService attachmentService;

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
	Page<ComponentDto> getAllPageable(@RequestParam Pageable pageable, 
			@RequestParam(required = false) String nameSearch, 
			@RequestParam(required = false) Long supplierId,
			@RequestParam(required = false) Long itemId,
			@RequestParam(required = false) String unitFilter) {
		Page<Component> components = componentRepo.findPage(pageable, nameSearch, supplierId, itemId,unitFilter);
		Page<ComponentDto> dtos = components.map(component -> {
			ComponentDto dto = new ComponentDto();
			dto.setId(component.getId());
			dto.setNumber(component.getNumber());
			dto.setName(component.getName());
			dto.setCategoryName(component.getCategory()==null?"":component.getCategory().getName());
			dto.setSupplierName(component.getSupplier()==null?"":component.getSupplier().getName());
			dto.setSupplierId(component.getSupplier()==null?null:component.getSupplier().getId());
			dto.setUnitsSoldNotProd(component.getUnitsSoldNotProd());
			dto.setUnitsOnStock(component.getUnitsOnStock());
			dto.setUnitsOrdered(component.getUnitsOrdered());
			dto.setUnitsLocked(component.getUnitsLocked());
			dto.setUnitsShort(component.getUnitsShort());
			dto.setUnitCost(component.getUnitCost());
			dto.setUnitsForProduction(component.getUnitsForProduction());
			dto.setUnitsForSale(component.getUnitsForSale());
			dto.setUnitsReceived(component.getUnitsReceived());
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

	@GetMapping("/components/dto")
	List<ComponentDto> get(@RequestParam Long[] componentIds) {
		Iterable<Component> components = componentRepo.findAllById(Arrays.asList(componentIds));
		List<ComponentDto> dtos = new ArrayList<ComponentDto>();
		components.forEach(component -> {
			ComponentDto dto = new ComponentDto();
			dto.setId(component.getId());
			dto.setNumber(component.getNumber());
			dto.setName(component.getName());
			dto.setCategoryName(component.getCategory()==null?"":component.getCategory().getName());
			dto.setSupplierName(component.getSupplier()==null?"":component.getSupplier().getName());
			dto.setSupplierId(component.getSupplier().getId());
			dto.setUnitsSoldNotProd(component.getUnitsSoldNotProd());
			dto.setUnitsOnStock(component.getUnitsOnStock());
			dto.setUnitsOrdered(component.getUnitsOrdered());
			dto.setUnitsLocked(component.getUnitsLocked());
			dto.setUnitsShort(component.getUnitsShort());
			dto.setUnitCost(component.getUnitCost());
			dto.setUnitsForProduction(component.getUnitsForProduction());
			dto.setUnitsForSale(component.getUnitsForSale());
			dto.setUnitsReceived(component.getUnitsReceived());
			dtos.add(dto);
		});
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
	ResponseEntity<?> postComponentAndAttachment(@RequestParam(required = false) MultipartFile image, @RequestParam String jsonComponent) throws JsonParseException, JsonMappingException, IOException{
		Component component = objectMapper.readValue(jsonComponent, Component.class);
		if(!component.getNumber().matches("^[a-zA-Z0-9\\-]{1,15}$")) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Component Number is invalid. Alphanumeric and hyphen only allowed. Maximum 15 characters.");
		}
		Long id = componentRepo.getIdByNumber(component.getNumber());
		if((component.getId()==null && id !=null) || (component.getId()!=null && id !=null && !component.getId().equals(id))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Component Number already exists. Please, choose differrent.");
		}
		component = componentService.save(component);
		if(image!=null) {
			Attachment attachment = attachmentService.store(image, Component.class.getSimpleName(), component.getId(), component.getAttachment());
			component.setAttachment(attachment);
			component = componentService.save(component);
		}
		itemService.updateUnitsByComponent(component.getId());
		componentService.updateUnits(Arrays.asList(component.getId()));
		itemService.updateUnitsReadyProdByComponent(component.getId());
		return ResponseEntity.ok(component);
	}

	@DeleteMapping("/component/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Component component = componentRepo.findById(id).get();
		if(component.getItemComponents().size()>0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Items!");
		}
		if(component.getPurchaseComponents().size()>0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There are existing Purchases!");
		}
		componentService.delete(id);
		return ResponseEntity.ok().build();
	}
	
	//This is update
	@GetMapping("/component/update/units")
	ResponseEntity<?> postUpdateUnits() {
		try {
			componentService.updateUnits(null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok().body("OK");
	}
}