// src/main/java/com/example/demo/dto/DashboardStatsDTO.java
package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardStatsDTO {
    private long totalUsers;
    private long totalDealers;
    private long totalCustomers;
    private long totalProducts;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private int lowStockCount;
}
