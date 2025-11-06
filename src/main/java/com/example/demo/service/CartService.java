package com.example.demo.service;

import com.example.demo.dto.CartDTO;

public interface CartService {
    CartDTO addToCart(String customerId, Long productId, int quantity);
    CartDTO removeFromCart(String customerId, Long productId);
    CartDTO getCart(String customerId);
    void deleteCart(String customerId);
    CartDTO updateCartItemQuantity(String customerId, Long productId, int newQuantity);

}
