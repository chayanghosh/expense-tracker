package com.example.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.DTO.SignupDTO;
import com.example.expense_tracker.entity.Role;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	PasswordEncoder pwdEncoder;
	@Autowired
	UserRepository userRepo;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupDTO signupDTO){
		if(userRepo.findByEmail(signupDTO.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body("Email already registered");
		}
		User user = new User();
		user.setName(signupDTO.getEmail());
		user.setEmail(signupDTO.getEmail());
		user.setPassword(pwdEncoder.encode(signupDTO.getPassword()));
		user.setRole(Role.USER);
		
		userRepo.save(user);
		return ResponseEntity.ok("User Registered");
	}
}
