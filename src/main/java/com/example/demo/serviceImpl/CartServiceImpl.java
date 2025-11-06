package com.example.demo.serviceImpl;

import com.example.demo.dto.CartDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public CartDTO addToCart(String customerId, Long productId, int quantity){
        //Fetch customer
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found"));

        //fetch product
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));

        //Find existing cart or create new
        Cart cart=cartRepository.findByCustomer(customer)
                .orElseGet(()->{
                    Cart newCart=new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });
        //Check if item already exist in cart
        Optional<CartItem> existingItem=cart.getItems().stream()
                .filter(i->i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()){
            CartItem item=existingItem.get();
            item.setQuantity(item.getQuantity()+quantity);
            item.setSubTotal(product.getPrice()*item.getQuantity());
        }else {
            CartItem newItem=new CartItem();
            newItem.setProduct(product);
            newItem.setCart(cart);
            newItem.setQuantity(quantity);
            newItem.setSubTotal(product.getPrice()*quantity);
            cart.getItems().add(newItem);

        }
        //REcalculate total amt and total quantity
        double totalAmount=cart.getItems().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        cart.setTotalAmount(totalAmount);

        int totalQuantity=cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalQuantity(totalQuantity);

        //save cart and return dto
            Cart savedCart=cartRepository.save(cart);
            return CartMapper.toDTO(savedCart);
    }
    @Override
    public CartDTO removeFromCart(String customerId,Long productId){
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found"));

        Cart cart=cartRepository.findByCustomer(customer)
                .orElseThrow(()->new ResourceNotFoundException("cart not found"));

                cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

                double totalAmount=cart.getItems().stream()
                        .mapToDouble(CartItem::getSubTotal)
                        .sum();
                cart.setTotalAmount(totalAmount);

                int totalQuantity=cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();
                cart.setTotalQuantity(totalQuantity);

                Cart updatedCart=cartRepository.save(cart);
                return CartMapper.toDTO(updatedCart);
    }
    @Override
    public CartDTO getCart(String customerId){
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found"));

        Cart cart=cartRepository.findByCustomer(customer)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));
        return CartMapper.toDTO(cart);
    }
    @Override
    public void deleteCart(String customerId){
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found"));

        Cart cart=cartRepository.findByCustomer(customer)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));
        cartRepository.delete(cart);

    }
    @Override
    public  CartDTO updateCartItemQuantity(String customerId,Long productId,int newQuantity){
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("Customer not found"));

        Cart cart=cartRepository.findByCustomer(customer)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));

        //find item to update
        CartItem item=cart.getItems().stream()
                .filter(i->i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("Product not found in Cart"));

        //update quantity and subtotal
        item.setQuantity(newQuantity);
        item.setSubTotal(item.getProduct().getPrice()*newQuantity);

        //Recalculate total
        double totalAmount=cart.getItems().stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
        cart.setTotalAmount(totalAmount);

        int totalQuantity=cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalQuantity(totalQuantity);

        Cart updatedCart=cartRepository.save(cart);
        return CartMapper.toDTO(updatedCart);
    }

}
