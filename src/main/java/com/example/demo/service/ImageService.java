package com.example.demo.service;

import com.example.demo.entity.ImageFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    List<ImageFile> uploadImage(Long productId, String path, List<MultipartFile> files, Authentication authentication) throws IOException;


}
