package com.example.demo.service;

import com.example.demo.dto.CartDTO;

public interface CartService {
    CartDTO addToCart(Long customerId, Long productId, int quantity);
    CartDTO removeFromCart(Long customerId, Long productId);
    CartDTO getCart(Long customerId);
    void deleteCart(Long customerId);
    CartDTO updateCartItemQuantity(Long customerId, Long productId, int newQuantity);

}
