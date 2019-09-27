package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Role;
import com.noovitec.mpb.repo.RoleRepo;


@RestController
@RequestMapping("/api")
class RoleRest {

	private final Logger log = LoggerFactory.getLogger(RoleRest.class);
	private RoleRepo roleRepo;

	public RoleRest(RoleRepo roleRepo) {
		this.roleRepo = roleRepo;
	}

	@GetMapping("/role")
	Collection<Role> getAll() {
		return roleRepo.findAll();
	}

	@GetMapping("/role/{id}")
	ResponseEntity<Role> get(@PathVariable Long id) {
		Optional<Role> result = roleRepo.findById(id);
		return ResponseEntity.ok().body(result.get());
	}

	@PostMapping("/role")
	ResponseEntity<Role> post(@RequestBody(required = false) Role role) throws URISyntaxException {
		if (role == null) {
			role = new Role();
		}
		Role result = roleRepo.save(role);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/role/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		roleRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}