package com.noovitec.mpb.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.repo.ComponentTypeRepo;

@RestController
@RequestMapping("/api")
class RegisteryRest {

	private final Logger log = LoggerFactory.getLogger(RegisteryRest.class);

	@Autowired
	private ComponentTypeRepo componentTypeRepo;

	@GetMapping("/registery/componentType/kv")
	Collection<KeyValueDto> findAllComponentTypes(@RequestParam(required = false) Long categoryId) {
		return categoryId==null?componentTypeRepo.findAllKvs():componentTypeRepo.findAllKvsByCategory(categoryId);
	}


}