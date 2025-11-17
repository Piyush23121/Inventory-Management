package com.example.demo.serviceImpl;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Dealer;
import com.example.demo.entity.ImageFile;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.*;
import com.example.demo.service.ImageService;
import com.example.demo.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionLogService transactionLogService;
    @Autowired
    private DealerRepository dealerRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;


    @Override
    public ProductDTO addProduct(ProductDTO productDTO){
        //Covert dto to entity before saving
        Product product= ProductMapper.toEntity(productDTO);
        //Get login user email from jwt token
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        //Fetch user details from db
         Dealer dealer=dealerRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Dealer not found: "+email));

       //Auto assign dealer id
            product.setDealerId(dealer.getUserId());

//save product
        Product savedProduct=productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }
    @Override
    public ProductDTO getProductById(Long id){
        //fetch product by id from db
      Product product=productRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Product not found."));

      List<String> imageUrls=getImages(product);
      ProductDTO productDTO=ProductMapper.toDTO(product);
      productDTO.setImages(imageUrls);

      //return product
              return productDTO;
    }
    @Override
    public Page<ProductDTO> getAllProducts(String category,
                                           String brand,
                                           Double minPrice,
                                           Double maxPrice,
                                           Pageable pageable){
        Page<Product> products= productRepository.findAll(pageable);
        return new PageImpl<>(
                products.stream()
                        .filter(product ->category==null || product.getCategory().equalsIgnoreCase(category))
                        .filter(product ->brand==null || product.getBrand().equalsIgnoreCase(brand))
                        .filter(product ->minPrice==null || product.getPrice()>=minPrice)
                        .filter(product ->maxPrice==null || product.getPrice()<=maxPrice)
                        .map(ProductMapper::toDTO)
                        .collect(Collectors.toList()),
                pageable,
                products.getTotalElements()
        );
    }
    @Override
    public ProductDTO updateProduct(Long id,ProductDTO productDTO){
        //find product from db by id
        Product exitingProduct=productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+id));
        if(productDTO.getDescription()!=null)
            exitingProduct.setDescription(productDTO.getDescription());
        if(productDTO.getPrice()!=null)
            exitingProduct.setPrice(productDTO.getPrice());


        Product savedProduct = productRepository.save(exitingProduct);
        return ProductMapper.toDTO(savedProduct);
    }
    @Transactional
    @Override
    public void deleteProduct(Long id,Authentication authentication) throws IOException {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
//find the user that has to be del

        Product product=productRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+id));

        if (!product.getDealerId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("You are not allowed to delete this product");
        }
        List<ImageFile> imageFiles=imageRepository.findByProductId(product.getId());
        for (ImageFile imageFile : imageFiles) {
            Files.deleteIfExists(Paths.get(imageFile.getFilePath()));
        }
        imageRepository.deleteByProductId(id);
        cartItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
    @Override
    public ProductDTO updateStock(Long id , int quantityChange){
        Product product=productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+id));

        int newQuantity=product.getQuantity() + quantityChange;
        if (newQuantity<0){
           throw new ResourceNotFoundException("Insufficient stock. Current Stock: "+ product.getQuantity());
        }
        product.setQuantity(newQuantity);

        Product savedProduct=productRepository.save(product);

        //for saving transaction log
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username= (principal instanceof UserDetails)
                ?((UserDetails)principal).getUsername()
                :principal.toString();

        User user= userRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("User not found."));

        transactionLogService.saveLog(
                savedProduct.getId(),
                user.getId(),
                quantityChange>=0 ? "INCREASE" : "DECREASE",
                Math.abs(quantityChange)
        );

        return ProductMapper.toDTO(savedProduct);
    }
    @Override
    public List<ProductDTO> getLowStockProducts(){
        return productRepository.findAll()
                .stream()
                .filter(p->p.getQuantity()<p.getMinStockLevel())
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    private List<String> getImages(Product  product){
        List<String> images=new ArrayList<>();
        List<ImageFile> imageFiles=product.getImages();
        if(imageFiles==null || imageFiles.isEmpty()){
            String image="No images found";
            images.add(image);
        }else {
            for(ImageFile imageFile:imageFiles){
                String image=imageService.getImage(imageFile);
                images.add(image);
            }
        }
        return images;
    }

}


