package com.example.demo.serviceImpl;

import com.example.demo.entity.Dealer;
import com.example.demo.entity.ImageFile;
import com.example.demo.entity.Product;
import com.example.demo.repository.DealerRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        List<ImageFile> images=new ArrayList<>();
        String email=authentication.getName();
        Dealer dealer=dealerRepository.findByEmail(email).get();
        String dealerId= dealer.getDId();
        Product product=productRepository.findById(productId).get();

        for (MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            String uniqueName=System.currentTimeMillis()+"."+fileName;
            String folderPath=path+ File.separator+dealerId;

            File dir=new File(folderPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            String finalPath=folderPath+File.separator+uniqueName;
            Files.copy(file.getInputStream(), Paths.get(finalPath));

            ImageFile imageFile=new ImageFile();
            imageFile.setName(uniqueName);
            imageFile.setProduct(product);
            imageFile.setFilePath(finalPath);
            imageFile.setType(file.getContentType());
            ImageFile savedImage=imageRepository.save(imageFile);
        }
        product.setImages(images);
        productRepository.save(product);
        return images;





    }
}
