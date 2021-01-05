package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;

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

import com.noovitec.mpb.dto.ComponentDto;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.ComponentAdjustment;
import com.noovitec.mpb.repo.ComponentAdjustmentRepo;


@RestController
@RequestMapping("/api")
class ComponentAdjustmentRest {

	private final Logger log = LoggerFactory.getLogger(ComponentAdjustmentRest.class);
	ComponentAdjustmentRepo componentAdjustmentRepo;

	public ComponentAdjustmentRest(ComponentAdjustmentRepo componentAdjustmentRepo) {
		this.componentAdjustmentRepo = componentAdjustmentRepo;
	}
	
	@GetMapping("/componentAdjustment/{id}")
	ResponseEntity<?> get(@PathVariable Long id) {
		ComponentAdjustment component = componentAdjustmentRepo.findById(id).get();
		return ResponseEntity.ok().body(component);
	}

	@GetMapping("/componentAdjustment/component/{componentId}")
	Collection<ComponentAdjustment> getByComponent(@PathVariable Long componentId) {
		Collection<ComponentAdjustment> result = componentAdjustmentRepo.findByComponent(componentId);
		return result;
	}

	@GetMapping("/componentAdjustment/pageable")
	Page<ComponentDto> getAllPageable(@RequestParam Pageable pageable, 
			@RequestParam(required = false) Long componentId) {
		Page<ComponentAdjustment> components = componentAdjustmentRepo.findPage(pageable, componentId);
		Page<ComponentDto> dtos = components.map(component -> {
			ComponentDto dto = new ComponentDto();
			dto.setId(component.getId());
		    return dto;
		});
		return dtos;
	}

	@PostMapping("/componentAdjustment")
	ResponseEntity<?> post(@RequestBody(required = false) ComponentAdjustment componentAdjustment) throws URISyntaxException {
		ComponentAdjustment result = componentAdjustmentRepo.save(componentAdjustment);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/componentAdjustment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		componentAdjustmentRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}