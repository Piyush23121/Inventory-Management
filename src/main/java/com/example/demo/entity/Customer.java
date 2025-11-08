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
@Table(name = "customers")
public class Customer {


        @Id
        private String CId;

        @Column(nullable = false)
        private Long userId;

        private String name;
        private String email;
        private String password;
        private String mobileNo;
        private String address;


        @CreationTimestamp
        @Column(updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(insertable = false)
        private LocalDateTime updatedAt;

        @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
        private Cart cart;




    }
