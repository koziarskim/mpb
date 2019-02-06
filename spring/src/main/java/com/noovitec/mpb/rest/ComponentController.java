package com.noovitec.mpb.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.repo.ComponentRepo;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
class ComponentController {

    private final Logger log = LoggerFactory.getLogger(ComponentController.class);
    private ComponentRepo componentRepository;

    public ComponentController(ComponentRepo componentRepository) {
        this.componentRepository = componentRepository;
    }

    @GetMapping("/components")
    Collection<Component> components() {
        return componentRepository.findAll();
    }

    @GetMapping("/components/{id}")
    ResponseEntity<?> getComponent(@PathVariable Long id) {
        Optional<Component> component = componentRepository.findById(id);
        return component.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/components")
    ResponseEntity<Component> createComponent(@Valid @RequestBody Component component) throws URISyntaxException {
        log.info("Request to create component: {}", component);
        Component result = componentRepository.save(component);
        return ResponseEntity.created(new URI("/api/component/" + result.getId()))
                .body(result);
    }

    @PutMapping("/components")
    ResponseEntity<Component> updateComponent(@Valid @RequestBody Component component) {
        log.info("Request to update component: {}", component);
        Long id = component.getId();
        Optional<Component> existing_component = componentRepository.findById(id);
        if(existing_component.isPresent()) {
        	existing_component.get().setName(component.getName());
        	existing_component.get().setStockNumber(component.getStockNumber());
        	existing_component.get().setVendorStockNumber(component.getVendorStockNumber());
        	existing_component.get().setDescription(component.getDescription());
        	existing_component.get().setPicture(component.getPicture());
        	existing_component.get().setSupplier(component.getSupplier());
        	existing_component.get().setAssumedPrice(component.getAssumedPrice());
        }
        
        Component result = componentRepository.save(existing_component.get());
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/components/{id}")
    public ResponseEntity<?> deleteComponent(@PathVariable Long id) {
        log.info("Request to delete component: {}", id);
        componentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}