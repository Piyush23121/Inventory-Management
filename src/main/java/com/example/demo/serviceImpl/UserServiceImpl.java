
package com.example.demo.serviceImpl;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.*;
import com.example.demo.security.Jwt;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CartRepository cartRepository;

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
                    if (userDTO.getCompanyName() != null)
                        throw new AccessDeniedException("Company Name is Available for dealers only");
                    if (userDTO.getGstinNo() != null)
                        throw new AccessDeniedException("GSTIN Number is Available for dealers only");

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
                    if (userDTO.getCompanyName()==null||userDTO.getCompanyName().isBlank())
                        throw  new AccessDeniedException("Company name is required");
                    if (userDTO.getGstinNo()==null||userDTO.getGstinNo().isBlank())
                        throw  new AccessDeniedException("GSTIN NO is required");


                    dealerRepository.save(dealer);

                }
                case CUSTOMER -> {
                    if (userDTO.getCompanyName() != null)
                        throw new AccessDeniedException("Company Name is Available for dealers only");
                    if (userDTO.getGstinNo() != null)
                        throw new AccessDeniedException("GSTIN Number is Available for dealers only");

                    long customerCount = customerRepository.count();
                    Customer customer = UserMapper.toCustomer(user);
                    customer.setCId("C" + (customerCount + 1));
                    customer.setUserId(user.getId());
                    customerRepository.save(customer);
                }
            }
        }//returndto back
        int otp=createOtp();
        Otp otpUser = new Otp();
        otpUser.setEmail(userDTO.getEmail());
        otpUser.setOtp(otp);
        otpRepository.save(otpUser);

        emailService.sendEmail(userDTO.getEmail(),"OTP Verification",otp+"Verify Using This Otp and Do not Share It With Anyone");
        return UserMapper.toDTO(user);
    }
    @Override
    public void verifyOtp(String userOtp,String email){

        String str =String.valueOf(userOtp);
        if (!str.matches("^[0-9]{6}$")){
            throw new AccessDeniedException("Otp must be 6 Digits");
        }
        int otpValue=Integer.parseInt(userOtp);
        User user=userRepository.findByEmail(email).get();
        if (user==null){
            throw new AuthenticationFailureException("Email Not Registered");
        }
        if (user.isStatus()){
            throw new AccessDeniedException("Email Already Registered");
        }
        Otp storedOtp=otpRepository.findByEmail(email);
        if (storedOtp.getOtp()!=otpValue){
            throw new InvalidInputException("Invalid Otp , Please Enter correct otp");
        }




            user.setStatus(true);
            userRepository.save(user);
            otpRepository.delete(storedOtp);


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
        if(!user.isStatus())
            throw new UnverifiedEmail("plz verify otp");
// Generate jwt token using users email and password
        String token = jwt.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getRole().name());//return jwt response containing token and role
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Authentication authentication) throws IOException{
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

                    List<Product> products=productRepository.findByDealerId(id);
                    for (Product product : products) {
                        List<ImageFile> imageFiles=imageRepository.findByProductId(product.getId());
                        for (ImageFile imageFile : imageFiles) {
                            Files.deleteIfExists(Paths.get(imageFile.getFilePath()));
                        }
                            imageRepository.deleteByProductId(product.getId());
                        cartItemRepository.deleteByProductId(product.getId());

                    }
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
    private int createOtp() {
        Random random = new Random();
        int otp = 100000+random.nextInt(900000);
        return otp;
    }
}
