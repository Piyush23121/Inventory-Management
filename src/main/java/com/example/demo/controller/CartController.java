package com.example.demo.controller;


import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    //  Add product to cart
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/addToCart")
    public ResponseEntity<ResponseDto> addToCart(
            @RequestParam String customerId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        cartService.addToCart(customerId, productId, quantity);
        ResponseDto response = new ResponseDto("Success", "Product added to cart successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    //  Update product quantity in cart
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PatchMapping("/updateQuantity")
    public ResponseEntity<BaseResponseDTO<CartDTO>> updateCart(
            @RequestParam String customerId,
            @RequestParam Long productId,
            @RequestParam int newQuantity) {
        CartDTO updatedCart = cartService.updateCartItemQuantity(customerId, productId, newQuantity);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Cart updated successfully", updatedCart));
    }
    // Remove a specific product from cart
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping("/removeFromCart")
    public ResponseEntity<ResponseDto> removeFromCart(
            @RequestParam String customerId,
            @RequestParam Long productId) {
        cartService.removeFromCart(customerId, productId);
        ResponseDto response = new ResponseDto("Success", "Product removed from cart successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    //  Get cart for a customer
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getCart")
    public ResponseEntity<BaseResponseDTO<CartDTO>> getCart(@RequestParam String customerId) {
        CartDTO cart = cartService.getCart(customerId);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Cart fetched successfully", cart));
    }
    //  Clear entire cart
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping("/deleteCart")
    public ResponseEntity<ResponseDto> deleteCart(@RequestParam String customerId) {
        cartService.deleteCart(customerId);
        ResponseDto response = new ResponseDto("Success", "Cart cleared successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}