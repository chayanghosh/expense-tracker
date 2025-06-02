package com.example.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.DTO.LoginDTO;
import com.example.expense_tracker.DTO.SignupDTO;
import com.example.expense_tracker.entity.Role;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.security.JwtUtil;
import com.example.expense_tracker.security.MyUserDetails;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	PasswordEncoder pwdEncoder;
	@Autowired
	UserRepository userRepo;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtil jwtUtil;
	
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
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO){
//		if(userRepo.findByEmail(loginDTO.getEmail()).isPresent()) {
//			if(pwdEncoder.matches(loginDTO.getPassword(), userRepo.findByEmail(loginDTO.getEmail()).get().getPassword())){
//				return ResponseEntity.ok("User authenticated!");
//			}else {
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
//			}
//		}else
//			return ResponseEntity.badRequest().body("No such usernames in record");
		
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
					);
			MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
			String token = jwtUtil.generateToken(userDetails);
			
			return ResponseEntity.ok(token);
		}catch(BadCredentialsException e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization failed");
		}
	}
}
