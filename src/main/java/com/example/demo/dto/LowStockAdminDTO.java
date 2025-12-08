package com.example.demo.dto;

import lombok.Data;

@Data
public class LowStockAdminDTO {

    private Long productId;
    private String productName;

    private int quantity;
    private int minStockLevel;

    private Long dealerId;
    private String dealerName;
    private String dealerEmail;
}
