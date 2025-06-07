package com.example.expense_tracker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.DTO.LoginDTO;
import com.example.expense_tracker.DTO.SignupDTO;
import com.example.expense_tracker.entity.Role;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.security.JwtUtil;
import com.example.expense_tracker.security.MyUserDetails;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder pwdEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String signup(SignupDTO signupDTO) {
        if (userRepo.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(signupDTO.getEmail());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(pwdEncoder.encode(signupDTO.getPassword()));
        user.setRole(Role.USER);

        userRepo.save(user);
        return "User Registered";
    }

    @Override
    public String login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
                )
            );
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            return jwtUtil.generateToken(userDetails);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Authorization failed");
        }
    }
}

