package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController         //tells spring that this class handle http req and return json responce
@RequestMapping("/api")
@CrossOrigin(origins = " http://localhost:5173/")
public class    UserController {
    @Autowired   //injects class
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody UserDTO userDTO){ //take data from req body and covert it into user dto obj
        userService.registerUser(userDTO); //calls the service method to register user
         ResponseDto responseDto=new ResponseDto("Success","Otp Sent Successfully");
         return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);//rturn 200 ok resp with saved user details
    }
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDTO<JwtResponse>> loginUser(@RequestBody LoginRequest loginRequest) {//read json with email and password and put inside loginreq obj
            JwtResponse response = userService.loginUser(loginRequest);//calls service method validate user and genrate token
            return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Login Successfull" ,response));

        }
        @PreAuthorize("hasAnyAuthority('ADMIN','DEALER','CUSTOMER')")
        @DeleteMapping("/deleteUser")
        public ResponseEntity<ResponseDto> deleteUser(@RequestParam Long id, Authentication authentication)throws Exception{
        userService.deleteUser(id,authentication);
        ResponseDto responseDto=new ResponseDto("Success","User Deleted Successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
        }
        @PreAuthorize("hasAnyAuthority('ADMIN','DEALER','CUSTOMER')")
        @PatchMapping("/updateUser")
        public ResponseEntity<ResponseDto> updateUser( @RequestParam Long id, @Valid @RequestBody UpdateUserDTO userDTO,Authentication authentication){
        userService.updateUser(id,userDTO,authentication);
        ResponseDto responseDto=new ResponseDto("Success","User updated successfully");
        return ResponseEntity.ok(responseDto);

        }
        @PostMapping("/verifyWithOtp")
        public ResponseEntity<ResponseDto> verifyOtp(@RequestParam String  otp,@RequestParam String email){
        userService.verifyOtp(otp,email);
        ResponseDto responseDto=new ResponseDto("Success","Otp verified successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }

        @PostMapping("/forgotPassword")
    public ResponseEntity<ResponseDto> forgotPassword(@RequestParam String email){
        userService.forgotPassword(email);
        ResponseDto responseDto=new ResponseDto("Success","Otp Sent Successfully to Email");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }

        @PostMapping("/resetPassword")
        public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody ResetPassDTO resetPassDTO){
        userService.resetPassword(resetPassDTO);
        ResponseDto responseDto=new ResponseDto("Success","Password updated Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }

        @PostMapping("/resendOtp")
    public ResponseEntity<ResponseDto> resendOtp(@RequestParam String email){
        userService.forgotPassword(email);
        ResponseDto responseDto=new ResponseDto("Success","OTP Resend!");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }



    }

