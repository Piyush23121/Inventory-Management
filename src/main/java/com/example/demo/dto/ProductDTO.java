package com.example.demo.dto;

import com.example.demo.entity.User;

import java.time.LocalDateTime;

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

    public ProductDTO(){}

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }
    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Long getDealerId() {
        return dealerId;
    }
    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
