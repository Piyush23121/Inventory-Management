package com.example.demo.repository;

import com.example.demo.dto.LowStockAdminDTO;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    void deleteProductsByDealerId(Long dealerId);

    List<Product> findByDealerId(Long dealerId);
    // count low stock items: uses JPQL or @Query
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity < p.minStockLevel")
    long countLowStock();




}
