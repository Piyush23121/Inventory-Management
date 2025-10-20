package com.example.demo.service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.exception.AuthenticationFailureException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Jwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setName("John");
        userDTO.setEmail("john@example.com");
        userDTO.setPassword("pass");
        userDTO.setRole(RoleType.CUSTOMER);

        user = UserMapper.toEntity(userDTO);
        user.setId(42L);
    }

    @Test
    void registerUser_success_returnsDto() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(42L);
            return u;
        });

        var result = userService.registerUser(userDTO);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_emailExists_throws() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        assertThrows(AuthenticationFailureException.class, () -> userService.registerUser(userDTO));
    }

    @Test
    void loginUser_success_returnsJwtResponse() {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("pass");

        User persisted = UserMapper.toEntity(userDTO);
        persisted.setId(42L);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(persisted));
        when(jwt.generateToken("john@example.com", persisted.getRole().name())).thenReturn("token-xyz");

        JwtResponse resp = userService.loginUser(req);

        assertNotNull(resp);
        assertEquals("token-xyz", resp.getToken());
        assertEquals(persisted.getRole().name(), resp.getRole());
    }

    @Test
    void loginUser_badEmail_throws() {
        LoginRequest req = new LoginRequest();
        req.setEmail("missing@example.com");
        req.setPassword("x");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(AuthenticationFailureException.class, () -> userService.loginUser(req));
    }

    @Test
    void loginUser_badPassword_throws() {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("wrong");

        User persisted = UserMapper.toEntity(userDTO);
        persisted.setId(42L);
        // persisted password is "pass"
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(persisted));

        assertThrows(AuthenticationFailureException.class, () -> userService.loginUser(req));
    }
}