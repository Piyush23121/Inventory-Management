package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.dto.CartDTO;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/addToCart")
    public ResponseEntity<BaseResponseDTO<CartDTO>> addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        CartDTO cart = cartService.addToCart(authentication, productId, quantity);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Added to cart", cart));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping("/updateQuantity")
    public ResponseEntity<BaseResponseDTO<CartDTO>> updateQuantity(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int newQuantity) {

        CartDTO updated = cartService.updateCartItemQuantity(authentication, productId, newQuantity);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Updated quantity", updated));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping("/removeFromCart")
    public ResponseEntity<BaseResponseDTO<CartDTO>> removeFromCart(
            Authentication authentication,
            @RequestParam Long productId) {

        CartDTO updated = cartService.removeFromCart(authentication, productId);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Item removed", updated));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getCart")
    public ResponseEntity<BaseResponseDTO<CartDTO>> getCart(Authentication authentication) {
        CartDTO cart = cartService.getCart(authentication);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Cart fetched", cart));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping("/deleteCart")
    public ResponseEntity<BaseResponseDTO<String>> deleteCart(Authentication authentication) {
        cartService.deleteCart(authentication);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Cart cleared", null));
    }
}
