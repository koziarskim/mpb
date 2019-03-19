package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Role;
import com.noovitec.mpb.entity.User;
import com.noovitec.mpb.repo.RoleRepo;
import com.noovitec.mpb.repo.UserRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class UserRest {

	private final Logger log = LoggerFactory.getLogger(UserRest.class);
	private UserRepo userRepo;
	@Autowired
	private RoleRepo roleRepo;

	public UserRest(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@GetMapping("/user")
	Collection<User> getAll() {
		return userRepo.findAll();
	}

	@GetMapping("/user/{id}")
	ResponseEntity<User> get(@PathVariable Long id) {
		Optional<User> result = userRepo.findById(id);
		return ResponseEntity.ok().body(result.get());
	}

	@PostMapping("/user")
	ResponseEntity<User> post(@RequestBody(required = false) User user) throws URISyntaxException {
		if (user == null) {
			user = new User();
		}
//		Collection<Role> roles = new HashSet<Role>();
//		for(Role role : user.getRoles()) {
//			Role r = roleRepo.getOne(role.getId());
//			roles.add(r);
//		}
//		user.setRoles(roles);
		User result = userRepo.save(user);
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		userRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}