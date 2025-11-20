package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    //Admin - add product
    @PreAuthorize("hasAuthority('DEALER')")
    @PostMapping("/addProducts")
    public ResponseEntity<ResponseDto> addProduct(@RequestBody ProductDTO dto){
        productService.addProduct(dto);
        ResponseDto responseDto=new ResponseDto("success","Product added Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // All Roles - View Products
    @GetMapping("/getProduct")
    public ResponseEntity<BaseResponseDTO<ProductDTO>> getProductById (@RequestParam Long id){
       ProductDTO product=productService.getProductById(id);
       return ResponseEntity.ok(new BaseResponseDTO<>("Success","Product fetched Successfully",product));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','DEALER','CUSTOMER')")
    @GetMapping("/getAllProducts")
    public ResponseEntity<BaseResponseDTO<Page<ProductDTO>>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @ParameterObject  Pageable pageable ){
        Page<ProductDTO> products=productService.getAllProducts(category, brand, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(new BaseResponseDTO<>("Success","Products fetched Successfully", products));
    }

    @PreAuthorize("hasAnyAuthority('DEALER')")
    @PatchMapping("/updateProduct")
    public ResponseEntity<ResponseDto> updateProduct(@RequestParam Long id, @RequestBody ProductDTO productDTO){
        ProductDTO updated=productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(new ResponseDto("Success", "Product Updated Successfully"));
    }

    @PreAuthorize("hasAnyAuthority('DEALER')")
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<ResponseDto> deleteProduct(@RequestParam Long id, Authentication authentication)throws IOException {
        productService.deleteProduct(id, authentication);
         ResponseDto responseDto=new ResponseDto("Success","Product Deleted Successfully");
         return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PreAuthorize("hasAuthority('DEALER')")
    @PatchMapping("/updateStock")
    public ResponseEntity<ResponseDto> updateStock(@RequestParam Long id, @RequestParam int quantityChange,Authentication authentication){
        ProductDTO updated= productService.updateStock(id,quantityChange,authentication);
        return ResponseEntity.ok(new ResponseDto("Success","Product Updated Successfully"));
    }

    @PreAuthorize("hasAnyAuthority('DEALER')")
    @GetMapping("/low-stock")
    public ResponseEntity<BaseResponseDTO<List<ProductDTO>>> getLowStockProducts(){
        List<ProductDTO> lowStock = productService.getLowStockProducts();
        return ResponseEntity.ok(new BaseResponseDTO<>("Success","Low stock Product fetched",lowStock));
    }


}

