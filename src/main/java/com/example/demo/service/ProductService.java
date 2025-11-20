package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO);

    ProductDTO getProductById(Long id);

    Page<ProductDTO> getAllProducts(String category,
                                    String brand,
                                    Double minPrice,
                                    Double maxPrice,
                                    Pageable pageable);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id, Authentication authentication) throws IOException;

    ProductDTO updateStock(Long id , int quantityChange,Authentication authentication) ;

    List<ProductDTO> getLowStockProducts();
};
