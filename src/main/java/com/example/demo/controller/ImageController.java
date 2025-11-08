package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.entity.ImageFile;
import com.example.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Value("${project.image}")
    private String path;

    @PreAuthorize("hasAuthority('DEALER')")
    @PostMapping("/uploadImage")
    public ResponseEntity<BaseResponseDTO> uploadImage(@RequestParam("file") List<MultipartFile> files, Long productId, Authentication authentication)throws IOException {
        List<ImageFile> imageFiles=imageService.uploadImage(productId,path,files,authentication);
        return  ResponseEntity.ok(new BaseResponseDTO<>("Success","Image Uploaded Successfully",imageFiles));
    }

}
