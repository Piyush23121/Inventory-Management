package com.example.demo.service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.AuthenticationFailureException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Jwt jwt;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) { //checks whether user email already exists
            throw new AuthenticationFailureException("Email Already Registered");
        }
        if(userRepository.existsByMobileNo(userDTO.getMobileNo())){
            throw new AuthenticationFailureException("Mobile number Already Registered");
        }
        User user = UserMapper.toEntity(userDTO);//convert user dto to entity using mapper

        //Encode pass before saving
        user.setPassword((passwordEncoder.encode(userDTO.getPassword())));
        if (user.getRole() == null) {
            user.setRole(RoleType.CUSTOMER); //if no role provided se default customer
        }
        user = userRepository.save(user);// save user to db
        return UserMapper.toDTO(user);//convert saved user back to dto and return it
    }

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {
        //fetch user by email from db
        //<optional> check user may or may not exist
        Optional<User> optUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optUser.isEmpty())
            throw new AuthenticationFailureException("Invalid email or password");

        User user = optUser.get(); //take user obj out from optional

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))//Check provided pass matches stored one
            throw new AuthenticationFailureException("Invalid email or password");
// Generate jwt token using users email and password
        String token = jwt.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getRole().name());//return jwt response containing token and role
    }

    @Override
    public void deleteUser(Long id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        User targetedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        RoleType currentRole = currentUser.getRole();

        if (currentRole == RoleType.ADMIN) {
            userRepository.delete(targetedUser);
            return;
        }
        if (currentUser.getId().equals(targetedUser.getId())) {
            userRepository.delete(targetedUser);
        } else {
            throw new AccessDeniedException("You are not allowed to delete other user");
        }


    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO, Authentication authentication) {
        //Get currently logged in user
        String currentEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

//get user to be updated
        User targetedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

//check role and give permission
        RoleType currentRole = currentUser.getRole();

        if (currentRole == RoleType.ADMIN || currentUser.getId().equals(targetedUser.getId())) {

            if (userDTO.getName() != null) targetedUser.setName(userDTO.getName());
            if (userDTO.getEmail() != null) targetedUser.setEmail(userDTO.getEmail());
            if (userDTO.getMobileNo() != null) targetedUser.setMobileNo(userDTO.getMobileNo());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPass = passwordEncoder.encode(userDTO.getPassword());
            targetedUser.setPassword(encodedPass);

        }
        //save and return updated user
            User updatedUser = userRepository.save(targetedUser);
            return UserMapper.toDTO(updatedUser);
        } else {
            throw new AccessDeniedException("You are not allowed to update other User");
        }
    }
}