package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPassDTO {
    public String email;
    private String otp;
    private String newPassword;
}
