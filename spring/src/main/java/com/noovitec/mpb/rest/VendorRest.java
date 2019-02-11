package com.noovitec.mpb.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Vendor;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.VendorRepo;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
class VendorRest {

    private final Logger log = LoggerFactory.getLogger(VendorRest.class);
    private VendorRepo vendorRepo;

    public VendorRest(VendorRepo vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    @GetMapping("/vendor")
    Collection<Vendor> getAll() {
        return vendorRepo.findAll();
    }
}