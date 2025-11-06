package com.example.demo.dto;

import com.example.demo.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Product product;
    private int quantity;
    private double subTotal;

}
