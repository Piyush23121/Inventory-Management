package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dealers")
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long DId;

    private String name;
    private String email;
    private String password;
    private String mobileNo;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String gstinNo;
    private String address;
    private String status;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private Long id;

}
