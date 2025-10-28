package com.example.demo.mapper;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    //convert product entity to product dto


        // Convert Product entity to ProductDTO
        public static ProductDTO toDTO(Product product) {
            if (product == null) return null;

            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setCategory(product.getCategory());
            dto.setBrand(product.getBrand());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setQuantity(product.getQuantity());
            dto.setMinStockLevel(product.getMinStockLevel());
            dto.setDealerId(product.getDealerId());
            dto.setCreatedAt(product.getCreatedAt());
            dto.setUpdatedAt(product.getUpdatedAt());

            return dto;
        }

        // Convert ProductDTO to Product entity
        public static Product toEntity(ProductDTO dto) {
            if (dto == null) return null;

            Product product = new Product();
            product.setId(dto.getId());
            product.setName(dto.getName());
            product.setCategory(dto.getCategory());
            product.setBrand(dto.getBrand());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setMinStockLevel(dto.getMinStockLevel());
//            product.setDealerId(dto.getDealerId());
            product.setCreatedAt(dto.getCreatedAt());
            product.setUpdatedAt(dto.getUpdatedAt());

            return product;
        }
    }
