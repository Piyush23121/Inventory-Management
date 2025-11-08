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
    private String DId;

    @Column(nullable = false)
    private Long userId;

    private String name;
    private String email;
    private String password;
    private String mobileNo;

    private String companyName;
    private String gstinNo;

    private String address;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;



}
