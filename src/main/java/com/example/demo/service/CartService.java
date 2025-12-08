package com.example.demo.service;

import com.example.demo.dto.CartDTO;
import org.springframework.security.core.Authentication;

public interface CartService {
    CartDTO addToCart(Authentication auth, Long productId, int quantity);

    CartDTO getCart(Authentication auth);

    CartDTO updateCartItemQuantity(Authentication auth, Long productId, int qty);

    CartDTO removeFromCart(Authentication auth, Long productId);

    void deleteCart(Authentication auth);
}
