package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ResetPassDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email format")
    @Schema(description = "Registered Email Id", example = "user@gmail.com")
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    @Schema(description = "6 digit OTP sent to email", example = "123456")
    private String otp;

    @NotBlank(message = "New Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain 8 chars, 1 uppercase, 1 lowercase, 1 number & 1 special char"
    )
    @Schema(description = "New Password", example = "NewPass@123")
    private String newPassword;
}