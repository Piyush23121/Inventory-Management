package com.example.demo.dto;

import com.example.demo.entity.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format ")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
             message="Please Enter Valid Email format")
    @Schema(description = "User Email Address",
            example="abcd@gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message="Please Enter Valid Password format, e.g Abcd@123")
    @Schema(description = "Enter Password",
            example="Abcd@123")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$",
             message="Please Enter Valid 10 digit Mobile No, e.g 7665671265")
    @Schema(description = "Enter 10 digit Valid Mobile No",
            example="9766431234")
    private String mobileNo;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Role is required")
    private RoleType role;



    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    // Dealer-specific fields (optional for other roles)
    private String companyName;
    private String gstinNo;
}