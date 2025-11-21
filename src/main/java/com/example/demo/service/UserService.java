package com.example.demo.service;

import com.example.demo.dto.*;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    JwtResponse loginUser(LoginRequest loginRequest);
    void deleteUser(Long id, Authentication authentication) throws IOException;
    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO, Authentication authentication);
    void verifyOtp(String userOtp,String email);
    void forgotPassword(String email) ;
    void resetPassword(ResetPassDTO resetPassDTO);

}
