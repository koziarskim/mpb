package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.noovitec.mpb.dto.ComponentAdjustmentListDto;
import com.noovitec.mpb.entity.ComponentAdjustment;
import com.noovitec.mpb.repo.ComponentAdjustmentRepo;
import com.noovitec.mpb.service.ComponentService;


@RestController
@RequestMapping("/api")
class ComponentAdjustmentRest {

	private final Logger log = LoggerFactory.getLogger(ComponentAdjustmentRest.class);
	private ComponentAdjustmentRepo componentAdjustmentRepo;
	@Autowired
	private ComponentService componentService;
	
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
	Page<ComponentAdjustmentListDto> getAllPageable(@RequestParam Pageable pageable, 
			@RequestParam(required = false) Long componentId) {
		Page<ComponentAdjustment> caList = componentAdjustmentRepo.findPage(pageable, componentId);
		Page<ComponentAdjustmentListDto> dtos = caList.map(ca -> {
			ComponentAdjustmentListDto dto = new ComponentAdjustmentListDto();
			dto.setId(ca.getId());
			dto.setDate(ca.getDate());
			dto.setNotes(ca.getNotes());
			dto.setReason(ca.getReason());
			dto.setUnitsAdjusted(ca.getUnitsAdjusted());
			dto.setComponentId(ca.getComponent().getId());
			dto.setComponentNumber(ca.getComponent().getNumber());
			dto.setComponentName(ca.getComponent().getName());
		    return dto;
		});
		return dtos;
	}

	@PostMapping("/componentAdjustment")
	ResponseEntity<?> post(@RequestBody(required = false) ComponentAdjustment componentAdjustment) throws URISyntaxException {
		componentAdjustment = componentAdjustmentRepo.save(componentAdjustment);
		componentService.updateUnits(Arrays.asList(componentAdjustment.getComponent().getId()));
		return ResponseEntity.ok().body(componentAdjustment);
	}

	@DeleteMapping("/componentAdjustment/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		ComponentAdjustment ca = componentAdjustmentRepo.findById(id).get();
		Long componentId = ca.getComponent().getId();
		componentAdjustmentRepo.deleteById(id);
		componentService.updateUnits(Arrays.asList(componentId));
		return ResponseEntity.ok().build();
	}
}