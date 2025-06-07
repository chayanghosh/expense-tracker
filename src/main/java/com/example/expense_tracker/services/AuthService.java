package com.example.expense_tracker.services;

import com.example.expense_tracker.DTO.LoginDTO;
import com.example.expense_tracker.DTO.SignupDTO;

public interface AuthService {
    String signup(SignupDTO signupDTO);
    String login(LoginDTO loginDTO);
}
