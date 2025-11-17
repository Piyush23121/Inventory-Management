package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cartItems")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Each item refer to one product
    @ManyToOne
    @JoinColumn(name = "productId",nullable = false)
    private Product product;

    //Each item refers to one cart
    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;

    //no of unit for this product
    @Column(nullable = false)
    private int quantity;

    //subtotal=product price * quantity
    @Column(nullable = false)

    private double subTotal;


}
