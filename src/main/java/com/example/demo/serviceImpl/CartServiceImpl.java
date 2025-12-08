package com.example.demo.serviceImpl;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.*;
import com.example.demo.service.CartService;
import com.example.demo.service.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {


    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public CartDTO addToCart(Authentication authentication, Long productId, int quantity) {

        String email = authentication.getName();

        // find User by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // find Customer by userId
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // find or create cart
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCustomer(customer);
                    return cartRepository.save(c);
                });

        // check if item exists
        Optional<CartItem> existing = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setSubTotal(product.getPrice() * item.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setCart(cart);
            newItem.setQuantity(quantity);
            newItem.setSubTotal(product.getPrice() * quantity);
            cart.getItems().add(newItem);
        }

        // recalc totals
        double totalAmount = cart.getItems().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        int totalQuantity = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        cart.setTotalAmount(totalAmount);
        cart.setTotalQuantity(totalQuantity);

        // save and return
        Cart saved = cartRepository.save(cart);
        return CartMapper.toDTO(saved);
    }

    @Override
    public CartDTO getCart(Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCustomer(customer);
                    return cartRepository.save(c);
                });

        return CartMapper.toDTO(cart);
    }

    // -------------------------
    // 3) UPDATE QUANTITY
    // -------------------------
    @Override
    public CartDTO updateCartItemQuantity(Authentication auth, Long productId, int newQuantity) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not in cart"));

        if (newQuantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQuantity);
            item.setSubTotal(item.getProduct().getPrice() * newQuantity);
        }

        recalcCart(cart);

        return CartMapper.toDTO(cartRepository.save(cart));
    }

    // -------------------------
    // 4) REMOVE ITEM
    // -------------------------
    @Override
    public CartDTO removeFromCart(Authentication auth, Long productId) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        recalcCart(cart);

        return CartMapper.toDTO(cartRepository.save(cart));
    }

    // -------------------------
    // 5) CLEAR CART
    // -------------------------
    @Override
    public void deleteCart(Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cart.setTotalAmount(0);
        cart.setTotalQuantity(0);

        cartRepository.save(cart);
    }

    // -------------------------
    // Helper to recalc totals
    // -------------------------
    private void recalcCart(Cart cart) {
        cart.setTotalAmount(cart.getItems().stream().mapToDouble(CartItem::getSubTotal).sum());
        cart.setTotalQuantity(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum());
    }
}