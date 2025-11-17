package com.example.demo.mapper;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import com.example.demo.serviceImpl.ProductServiceImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class CartMapper {

    //converrt cart to dto
    public static CartDTO toDTO(Cart cart){
        CartDTO dto=new CartDTO();
        dto.setId(cart.getId());
        dto.setCustomerId(cart.getCustomer().getUserId());
        dto.setTotalQuantity(cart.getTotalQuantity());
        dto.setTotalAmount(cart.getTotalAmount());
       List<CartItemDTO>  productDTOS=new ArrayList<>();
        List<CartItem> items = cart.getItems();
        for (CartItem item : items) {
            Product product = item.getProduct();
            ProductService service=new ProductServiceImpl();
            ProductDTO productById = service.getProductById(product.getId());
            CartItemDTO cartItemDTO=new CartItemDTO();
            cartItemDTO.setProduct(productById);
            cartItemDTO.setQuantity(item.getQuantity());
            productDTOS.add(cartItemDTO);
        }
        dto.setItems(productDTOS);


        return dto;
    }

    //dto to cart
    public Cart toEntity(CartDTO dto, Customer customer, List<CartItem> items){
        Cart cart=new Cart();
        cart.setCustomer(customer);
        cart.setItems(items);
        cart.setTotalAmount(dto.getTotalAmount());
        cart.setTotalQuantity(dto.getTotalQuantity());
        return cart;
    }

    //cartItem to dto
    public static CartItemDTO toCartItemDTO(CartItem item){
        CartItemDTO dto=new CartItemDTO();
        Product product = item.getProduct();

        dto.setQuantity(item.getQuantity());
        dto.setSubTotal(item.getSubTotal());

        return dto;
    }

    //dto to cartitem
    public CartItem toCartItem(CartItemDTO dto ,Product product, Cart cart){
        CartItem item=new CartItem();
        item.setProduct(product);
        item.setCart(cart);
        item.setQuantity(dto.getQuantity());
        item.setSubTotal(product.getPrice()* dto.getQuantity());
        return item;

    }
}
