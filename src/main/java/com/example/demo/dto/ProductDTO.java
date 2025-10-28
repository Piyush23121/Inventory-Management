package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String category;
    private String brand;
    private String description;
    private Double price;
    private Integer quantity;
    private Integer minStockLevel;
    private Long dealerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
