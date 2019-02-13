package com.noovitec.mpb.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ComponentRest {

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AttachmentRepo attachmentRepo;
	
	private final Logger log = LoggerFactory.getLogger(ComponentRest.class);
	private ComponentRepo componentRepository;

	public ComponentRest(ComponentRepo componentRepository) {
		this.componentRepository = componentRepository;
	}

	@GetMapping("/component")
	Collection<Component> getAll() {
		return componentRepository.findAll();
	}

	@GetMapping("/component/{id}")
	ResponseEntity<?> getComponent(@PathVariable Long id) {
		Optional<Component> component = componentRepository.findById(id);
		return component.map(response -> ResponseEntity.ok().body(response))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/component")
	ResponseEntity<Component> postComponent(@Valid @RequestBody Component component) throws URISyntaxException {
		Component result = componentRepository.save(component);
		return ResponseEntity.created(new URI("/api/component/" + result.getId())).body(result);
	}

	//This includes image upload.
	@PostMapping("/component/upload")
	ResponseEntity<Component> postComponentAndAttachment(@RequestParam(value="image", required=false) MultipartFile image, @RequestParam("jsonComponent") String jsonComponent) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		Component component = objectMapper.readValue(jsonComponent, Component.class);
		if(image!=null) {
			Attachment attachment = new Attachment();
			attachment.setData(image.getBytes());
			attachmentRepo.save(attachment);
			component.setAttachment(attachment);
		}
		Component result = componentRepository.save(component);
		return ResponseEntity.created(new URI("/api/component/" + result.getId())).body(result);
	}

	@DeleteMapping("/component/{id}")
	public ResponseEntity<?> deleteComponent(@PathVariable Long id) {
		Component component = componentRepository.getOne(id);
		if (component.getLocked()) {
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
					.body(Collections.singletonMap("message", "Component is currently locked"));
		}
		componentRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
}