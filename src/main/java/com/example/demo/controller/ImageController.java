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
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Value("${project.image}")
    private String path;

    @PreAuthorize("hasAuthority('DEALER')")
    @PostMapping("/uploadImage")
    public ResponseEntity<ResponseDto> uploadImage(@RequestParam("file") List<MultipartFile> files, Long productId, Authentication authentication) throws IOException {
        List<ImageFile> imageFiles = imageService.uploadImage(productId, path, files, authentication);
        ResponseDto responseDto = new ResponseDto("success", "Image added Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DEALER','CUSTOMER')")
    @GetMapping("getImage")
    public void getImage(@RequestParam String imageName, @RequestParam String dealerId, HttpServletResponse response, Authentication authentication) throws IOException {
        Path path1 = Paths.get(path, dealerId, imageName);
        response.setContentType(Files.probeContentType(path1));
        Files.copy(path1, response.getOutputStream());
    }


}
