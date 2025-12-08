package com.example.demo.serviceImpl;

import com.example.demo.entity.ImageFile;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Override
    public List<ImageFile> uploadImage(Long productId, String path, List<MultipartFile> files, Authentication authentication) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // folder = /uploads/{productId}
        String folderPath = path + File.separator + productId;

        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        List<ImageFile> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String uniqueName = System.currentTimeMillis() + "_" + originalName;

            String finalPath = folderPath + File.separator + uniqueName;

            Files.copy(file.getInputStream(), Paths.get(finalPath));

            ImageFile img = new ImageFile();
            img.setName(uniqueName);
            img.setProduct(product);
            img.setFilePath(finalPath);
            img.setType(file.getContentType());

            savedImages.add(imageRepository.save(img));
        }

        product.getImages().addAll(savedImages);
        productRepository.save(product);

        return savedImages;
    }

    @Override
    public String getImage(ImageFile imageFile) {
        Long productId = imageFile.getProduct().getId();

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/getImage")
                .queryParam("productId", productId)
                .queryParam("imageName", imageFile.getName())
                .toUriString();
    }
}
