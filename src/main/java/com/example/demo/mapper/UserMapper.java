package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Dealer;
import com.example.demo.entity.User;

public class UserMapper {
    // Convert User entity to UserDTO
    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobileNo(user.getMobileNo());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        return dto;
    }

    // Convert UserDTO to User entity
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setMobileNo(dto.getMobileNo());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        user.setStatus("ACTIVE");


        return user;
    }
    public static Admin toAdmin(User user){
        Admin admin=new Admin();
        admin.setName(user.getName());
        admin.setEmail(user.getEmail());
        admin.setPassword(user.getPassword());
        admin.setMobileNo(user.getMobileNo());
        admin.setAddress(user.getAddress());
        admin.setStatus("ACTIVE");
       // admin.setRole(user.getRole());

        return admin;
    }
    public static Dealer toDealer(User user,UserDTO userDTO){
        Dealer dealer =new Dealer();
        dealer.setAddress(user.getAddress());
        dealer.setEmail(user.getEmail());
        dealer.setStatus("ACTIVE");
        dealer.setPassword(user.getPassword());
        dealer.setName(user.getName());
        dealer.setMobileNo(user.getMobileNo());
        //dealer.setRole(user.getRole());
        dealer.setCompanyName(userDTO.getCompanyName());
        dealer.setGstinNo(userDTO.getGstinNo());

        return dealer;
    }
    public static Customer toCustomer(User user){
        Customer customer=new Customer();
        customer.setName(user.getName());
        customer.setEmail(user.getEmail());
        customer.setPassword(user.getPassword());
        customer.setMobileNo(user.getMobileNo());
        customer.setAddress(user.getAddress());
        customer.setStatus("ACTIVE");
       // customer.setRole(user.getRole());

        return customer;
    }
    public static Admin toAdmin(User user, Admin admin){

        admin.setName(user.getName());
        admin.setEmail(user.getEmail());
        admin.setPassword(user.getPassword());
        admin.setMobileNo(user.getMobileNo());
        admin.setAddress(user.getAddress());
        admin.setStatus(user.getStatus());
        //admin.setRole(user.getRole());

        return admin;
    }
    public static Customer toCustomer(User user,Customer customer){

        customer.setName(user.getName());
        customer.setEmail(user.getEmail());
        customer.setPassword(user.getPassword());
        customer.setMobileNo(user.getMobileNo());
        customer.setAddress(user.getAddress());
        customer.setStatus(user.getStatus());
       // customer.setRole(user.getRole());

        return customer;
    }
    public static Dealer toDealer(User user,UserDTO userDTO,Dealer dealer) {

        dealer.setAddress(user.getAddress());
        dealer.setEmail(user.getEmail());
        dealer.setStatus(user.getStatus());
        dealer.setPassword(user.getPassword());
        dealer.setName(user.getName());
        dealer.setMobileNo(user.getMobileNo());
       // dealer.setRole(user.getRole());
        dealer.setCompanyName(userDTO.getCompanyName());
        dealer.setGstinNo(userDTO.getGstinNo());

        return dealer;

    }
}
