package com.example.demo.repository;

import com.example.demo.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageFile,Long> {
    List<ImageFile> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
