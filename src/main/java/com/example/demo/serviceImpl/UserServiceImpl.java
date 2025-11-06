package com.example.demo.serviceImpl;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.AuthenticationFailureException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.*;
import com.example.demo.security.Jwt;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private Jwt jwt;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) { //checks whether user email already exists
            throw new AuthenticationFailureException("Email Already Registered");
        }
        if (userRepository.existsByMobileNo(userDTO.getMobileNo())) {
            throw new AuthenticationFailureException("Mobile number Already Registered");
        }
        //Encode Pass before saving
        String encodedPass = passwordEncoder.encode(userDTO.getPassword());
         //if role not provied the assign custumer
        RoleType role = (userDTO.getRole() != null) ? userDTO.getRole() : RoleType.CUSTOMER;

        // convert dto to user entity and assign encoded  pass and role

        User user = UserMapper.toEntity(userDTO);
        user.setPassword(encodedPass);
        user.setRole(role);
        user = userRepository.save(user); // Save in users table

        // Duplicate in role table
        synchronized (this) {  //prevent duplcate id

            switch (role) {
                case ADMIN -> {
                    long adminCount = adminRepository.count();
                    Admin admin = UserMapper.toAdmin(user);
                    admin.setAid("A" + (adminCount + 1));
                    admin.setUserId(user.getId());
                    adminRepository.save(admin);
                }
                case DEALER -> {
                    long dealerCount = dealerRepository.count();
                    Dealer dealer = UserMapper.toDealer(user, userDTO);
                    dealer.setDId("D" + (dealerCount + 1));
                    dealer.setUserId(user.getId());
                    dealerRepository.save(dealer);
                }
                case CUSTOMER -> {
                    long customerCount = customerRepository.count();
                    Customer customer = UserMapper.toCustomer(user);
                    customer.setCId("C" + (customerCount + 1));
                    customer.setUserId(user.getId());
                    customerRepository.save(customer);
                }
            }
        }//returndto back
        return UserMapper.toDTO(user);
    }

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {
        //fetch user by email from db
        //<optional> check user may or may not exist
        Optional<User> optUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optUser.isEmpty())
            throw new AuthenticationFailureException("Invalid email or password");

        User user = optUser.get(); //take user obj out from optional meanst ex
//Check provided pass matches stored one and use encoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new AuthenticationFailureException("Invalid email or password");
// Generate jwt token using users email and password
        String token = jwt.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getRole().name());//return jwt response containing token and role
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Authentication authentication) {
        //Idetify current login user
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
//find the user that has to be del
        User targetedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
//get users current role
        RoleType currentRole = currentUser.getRole();

        //admin del anyone and user can del them only
        if (currentRole == RoleType.ADMIN || currentUser.getId().equals(targetedUser.getId())) {

            //del spe role from child table first
            switch (targetedUser.getRole()) {
                case ADMIN -> adminRepository.deleteByUserId(id);
                case DEALER ->{
                    productRepository.deleteProductsByDealerId(id);
                    dealerRepository.deleteByUserId(id);
                }
                case CUSTOMER -> customerRepository.deleteByUserId(id);
            }//Del user record
            userRepository.deleteById(id);

        } else {
            throw new AccessDeniedException("You are not allowed to delete other user");
        }
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO, Authentication authentication) {
        //Get currently logged in user
        String currentEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

//get user to be updated
        User targetedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        //user can update itself only
        if (!currentUser.getId().equals(targetedUser.getId())) {
            throw new AccessDeniedException("Plz update your own account");
        }
//only update these fields
            if (userDTO.getName() != null) targetedUser.setName(userDTO.getName());
            if (userDTO.getEmail() != null) targetedUser.setEmail(userDTO.getEmail());
            if (userDTO.getMobileNo() != null) targetedUser.setMobileNo(userDTO.getMobileNo());
            if (userDTO.getStatus() != null) targetedUser.setStatus(userDTO.getStatus());
            if (userDTO.getAddress() != null) targetedUser.setAddress(userDTO.getAddress());

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                targetedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));


            }
            //save and return updated user
            User updatedUser = userRepository.save(targetedUser);


            //update same rcord in role table
            switch (updatedUser.getRole()) {
                case ADMIN -> {
                    Admin admin = adminRepository.findByUserId(updatedUser.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Admin record missing"));
                    admin = UserMapper.toAdmin(updatedUser, admin);
                    adminRepository.save(admin);
                }
                case DEALER -> {
                    Dealer dealer = dealerRepository.findByUserId(updatedUser.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Dealer record missing"));
                    dealer = UserMapper.toDealer(updatedUser, userDTO, dealer);
                    dealerRepository.save(dealer);
                }
                case CUSTOMER -> {
                    Customer customer = customerRepository.findByUserId(updatedUser.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Customer is missing"));
                    customer = UserMapper.toCustomer(updatedUser, customer);
                    customerRepository.save(customer);
                }

            }//return updated user dto
            return UserMapper.toDTO(updatedUser);
        }
    }
