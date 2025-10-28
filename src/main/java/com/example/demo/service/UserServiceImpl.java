package com.example.demo.service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.AuthenticationFailureException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Jwt;
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

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) { //checks whether user email already exists
            throw new AuthenticationFailureException("Email Already Registered");
        }
        if(userRepository.existsByMobileNo(userDTO.getMobileNo())){
            throw new AuthenticationFailureException("Mobile number Already Registered");
        }
        //Encode Pass
        String encodedPass=passwordEncoder.encode(userDTO.getPassword());

        RoleType role =(userDTO.getRole()!=null) ? userDTO.getRole() :RoleType.CUSTOMER;

        // Base User

        User user = new User();
        copyCommonFields(user, userDTO, encodedPass, role);
        user = userRepository.save(user); // Save in users table

        // Duplicate in role table

        switch (role) {
            case ADMIN -> {
                Admin admin = new Admin();
                copyCommonFields(admin, userDTO, encodedPass, role);
                admin.setId(user.getId());
                adminRepository.save(admin);

            }

            case DEALER -> {
                Dealer dealer = new Dealer();
                copyCommonFields(dealer, userDTO, encodedPass, role);
                dealer.setId(user.getId());
                dealerRepository.save(dealer);
            }
            case CUSTOMER -> {
                Customer customer = new Customer();
                copyCommonFields(customer, userDTO, encodedPass, role);
                customer.setId(user.getId());
                customerRepository.save(customer);
            }

        }

        return UserMapper.toDTO(user);

    }

    private void copyCommonFields(Object target, UserDTO dto, String encodedPassword, RoleType role) {
        if (target instanceof User u) {
            u.setName(dto.getName());
            u.setEmail(dto.getEmail());
            u.setPassword(encodedPassword);
            u.setMobileNo(dto.getMobileNo());
            u.setAddress(dto.getAddress());
            u.setStatus(dto.getStatus());
            u.setRole(role);

        } else if (target instanceof Admin a) {
            a.setName(dto.getName());
            a.setEmail(dto.getEmail());
            a.setPassword(encodedPassword);
            a.setMobileNo(dto.getMobileNo());
            a.setAddress(dto.getAddress());
            a.setStatus(dto.getStatus());
            a.setRole(role);

        } else if (target instanceof Dealer d) {
            d.setName(dto.getName());
            d.setEmail(dto.getEmail());
            d.setPassword(encodedPassword);
            d.setMobileNo(dto.getMobileNo());
            d.setAddress(dto.getAddress());
            d.setStatus(dto.getStatus());
            d.setRole(role);
        } else if (target instanceof Customer c) {
            c.setName(dto.getName());
            c.setEmail(dto.getEmail());
            c.setPassword(encodedPassword);
            c.setMobileNo(dto.getMobileNo());
            c.setAddress(dto.getAddress());
            c.setStatus(dto.getStatus());
            c.setRole(role);

        }

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
    @Transactional
    public void deleteUser(Long id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        User targetedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        RoleType currentRole = currentUser.getRole();

        //admin del anyone and user can del them only
        if (currentRole == RoleType.ADMIN || currentUser.getId().equals(targetedUser.getId())) {

            //del spe role from child table
            switch (targetedUser.getRole()) {
                case ADMIN -> adminRepository.deleteById(id);
                case DEALER -> dealerRepository.deleteById(id);
                case CUSTOMER -> customerRepository.deleteById(id);
            }
            userRepository.deleteById(id);

        } else {
            throw new AccessDeniedException("You are not allowed to delete other user");
        }
    }

    @Override
    @Transactional
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

        //update same rcord in role table
            switch (updatedUser.getRole()){
                case ADMIN -> {
                    Admin admin=adminRepository.findById(updatedUser.getId())
                            .orElseThrow(()-> new ResourceNotFoundException("Admin record missing"));
                    updateRole(admin,updatedUser);
                    adminRepository.save(admin);
                }
                case DEALER -> {
                    Dealer dealer=dealerRepository.findById(updatedUser.getId())
                            .orElseThrow(()-> new ResourceNotFoundException("Dealer record missing"));
                    updateRole(dealer,updatedUser);
                    dealerRepository.save(dealer);
                }
                case CUSTOMER -> {
                    Customer customer=customerRepository.findById(updatedUser.getId())
                            .orElseThrow(()-> new ResourceNotFoundException("Customer is missing"));
                    updateRole(customer,updatedUser);
                    customerRepository.save(customer);
                }
            }
            return UserMapper.toDTO(updatedUser);
        } else {
            throw new AccessDeniedException("You are not allowed to update other User");
        }
    }
    private void updateRole(Object target,User user){
        if (target instanceof Admin a){
            a.setName(user.getName());
            a.setEmail(user.getEmail());
            a.setPassword(user.getPassword());
            a.setMobileNo(user.getMobileNo());
            a.setAddress(user.getAddress());
            a.setStatus(user.getStatus());
        } else if (target instanceof Dealer d) {
            d.setName(user.getName());
            d.setEmail(user.getEmail());
            d.setPassword(user.getPassword());
            d.setMobileNo(user.getMobileNo());
            d.setAddress(user.getAddress());
            d.setStatus(user.getStatus());
        } else if (target instanceof Customer c) {
            c.setName(user.getName());
            c.setEmail(user.getEmail());
            c.setPassword(user.getPassword());
            c.setMobileNo(user.getMobileNo());
            c.setAddress(user.getAddress());
            c.setStatus(user.getStatus());

        }
    }
}