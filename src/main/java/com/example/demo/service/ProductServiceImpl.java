package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    TransactionLogService transactionLogService;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO){
        Product product= ProductMapper.toEntity(productDTO);
        //Get login user email from jwt token
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();

        //Fetch user details from db
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found: "+email));

       //Auto assign dealer id

            product.setDealerId(user.getId());


        Product savedProduct=productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }
    @Override
    public ProductDTO getProductById(Long id){
      Product product=productRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Product not found."));
              return ProductMapper.toDTO(product);
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
                        .filter(p ->category==null || p.getCategory().equalsIgnoreCase(category))
                        .filter(p ->brand==null || p.getBrand().equalsIgnoreCase(brand))
                        .filter(p ->minPrice==null || p.getPrice()>=minPrice)
                        .filter(p ->maxPrice==null || p.getPrice()<=maxPrice)
                        .map(ProductMapper::toDTO)
                        .collect(Collectors.toList()),
                pageable,
                products.getTotalElements()
        );
    }
    @Override
    public ProductDTO updateProduct(Long id,ProductDTO productDTO){
        Product exitingProduct=productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+id));
        exitingProduct.setName(productDTO.getName());
        exitingProduct.setDescription(productDTO.getDescription());
        exitingProduct.setPrice(productDTO.getPrice());
        exitingProduct.setCategory(productDTO.getCategory());

        Product savedProduct = productRepository.save(exitingProduct);
        return ProductMapper.toDTO(savedProduct);
    }
    @Override
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Product not found with id: "+id);
        }
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

}
