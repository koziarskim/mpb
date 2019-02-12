package com.noovitec.mpb.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Supplier;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.SupplierRepo;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
class SupplierRest {

    private final Logger log = LoggerFactory.getLogger(SupplierRest.class);
    private SupplierRepo supplierRepo;

    public SupplierRest(SupplierRepo supplierRepo) {
        this.supplierRepo = supplierRepo;
    }

    @GetMapping("/supplier")
    Collection<Supplier> getAll() {
        return supplierRepo.findAll();
    }
}