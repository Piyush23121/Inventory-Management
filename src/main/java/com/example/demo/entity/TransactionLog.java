// src/main/java/com/example/demo/entity/TransactionLog.java
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactionlog")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userID;
    private String changeType;
    private int quantityChanged;

    // amount for sale or value changed (use BigDecimal for money)
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
