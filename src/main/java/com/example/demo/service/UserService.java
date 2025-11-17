package com.example.demo.service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    JwtResponse loginUser(LoginRequest loginRequest);
    void deleteUser(Long id, Authentication authentication) throws IOException;
    UserDTO updateUser(Long id, UserDTO userDTO, Authentication authentication);
    void verifyOtp(String userOtp,String email);

}
