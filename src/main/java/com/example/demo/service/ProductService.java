package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    void deleteProduct(Long id);

    ProductDTO updateStock(Long id , int quantityChange);

    List<ProductDTO> getLowStockProducts();
};
