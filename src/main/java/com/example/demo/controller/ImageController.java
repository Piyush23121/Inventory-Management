package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.ImageFile;
import com.example.demo.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    @Value("${project.image}")
    private String path;

    @Autowired
    private ImageService imageService;

    @PreAuthorize("hasAuthority('DEALER')")
    @PostMapping("/uploadImage")
    public ResponseEntity<ResponseDto> uploadImage(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam Long productId,
            Authentication authentication) throws IOException {

        imageService.uploadImage(productId, path, files, authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto("success", "Images uploaded successfully"));
    }

    @GetMapping("/getImage")
    public void getImage(
            @RequestParam Long productId,
            @RequestParam String imageName,
            HttpServletResponse response) throws IOException {

        Path imagePath = Paths.get(path, productId.toString(), imageName);

        response.setContentType(Files.probeContentType(imagePath));
        Files.copy(imagePath, response.getOutputStream());
    }
}
