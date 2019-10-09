package com.noovitec.mpb.rest;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.app.MpbAuthenticationContext;
import com.noovitec.mpb.entity.User;
import com.noovitec.mpb.repo.UserRepo;


@RestController
@RequestMapping("/api")
class UserRest {

	private final Logger log = LoggerFactory.getLogger(UserRest.class);
	private UserRepo userRepo;
	@Autowired
	private MpbAuthenticationContext mpbAuthenticationContext;

	public UserRest(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@GetMapping("/user")
	ResponseEntity<Collection<User>> getAll() {
		return ResponseEntity.ok().body(userRepo.findAll());
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
		User result = userRepo.save(user);
		return ResponseEntity.ok().body(result);
	}

	@PostMapping("/user/login")
	ResponseEntity<?> login(@RequestBody(required = true) User user, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		User result = userRepo.getByUsername(user.getUsername());
		if (result == null) {
			log.info("Username not found");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username/password combination!");
		}
		if (!result.getPassword().equals(user.getPassword())) {
			log.info("Password is wrong");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username/password combination!");
		}
		log.info("Setting SID");
		//TODO: hash username or something....
		String sid = user.getUsername();
		mpbAuthenticationContext.addSid(sid);
		Cookie cookie = new Cookie("SID", sid);
		cookie.setPath("/");
		cookie.setMaxAge(3600); //1 hour.
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
//		response.setHeader("Set-Cookie", "SID="+sid+"; Path=/; Max-Age=3600; Secure; HttpOnly; SameSite=None");
		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		userRepo.deleteById(id);
		return ResponseEntity.ok().build();
	}
}