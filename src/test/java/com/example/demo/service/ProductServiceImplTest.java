//package com.example.demo.service;
//
//import com.example.demo.dto.ProductDTO;
//import com.example.demo.entity.Product;
//import com.example.demo.entity.User;
//import com.example.demo.exception.ResourceNotFoundException;
//import com.example.demo.mapper.ProductMapper;
//import com.example.demo.repository.ProductRepository;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.serviceImpl.ProductServiceImpl;
//import com.example.demo.serviceImpl.TransactionLogService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)//tells junit yo enable mokito
//class ProductServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private TransactionLogService transactionLogService;
//
//    @Mock
//    private SecurityContext securityContext;
//    @Mock
//    private Authentication authentication;
//    @Mock
//    private UserDetails userDetails;
//
//    @InjectMocks//teell mokito to to create real instance of productservice
//    private ProductServiceImpl productService;
//
//    private Product product;
//    private ProductDTO productDTO;
//    private User user;
//
//    @BeforeEach// runs before any test to intialize common obj
//    void setUp() {
//        // sample user
//        user = new User();
//        user.setId(100L);
//        user.setEmail("dealer@example.com");
//        user.setName("Dealer");
//
//        // sample product
//        product = new Product();
//        product.setId(1L);
//        product.setName("Phone");
//        product.setBrand("Samsung");
//        product.setCategory("Electronics");
//        product.setPrice(500.0);
//        product.setQuantity(10);
//        product.setMinStockLevel(3);
//
//        productDTO = ProductMapper.toDTO(product);
//
//        // configure SecurityContextHolder to use mocked securityContext (but don't pre-stub authentication here)
//        SecurityContextHolder.setContext(securityContext);
//    }
//
//    // ---------------- addProduct ----------------
//    @Test
//    void addProduct_success_assignsDealerAndSaves() {
//        // auth returns email
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn("dealer@example.com");
//
//        // user found
//        when(userRepository.findByEmail("dealer@example.com")).thenReturn(Optional.of(user));
//        // saving product returns product entity
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        ProductDTO saved = productService.addProduct(productDTO);
////the return dto is not null and namme mattches
//        assertNotNull(saved);
//        assertEquals("Phone", saved.getName());
//        //verify that save is called only once
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addProduct_userNotFound_throws() {
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn("test@example.com");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> productService.addProduct(productDTO));
//    }
//
//    // ---------------- getProductById ----------------
//    @Test
//    void getProductById_found_returnsDto() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        ProductDTO dto = productService.getProductById(1L);
//
//        assertNotNull(dto);
//        assertEquals("Phone", dto.getName());
//        verify(productRepository).findById(1L);
//    }
//
//    @Test
//    void getProductById_notFound_throws() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
//    }
//
//    // ---------------- getAllProducts ----------------
//    @Test
//    void getAllProducts_filtersApplied_returnPage() {
//        Page<Product> page = new PageImpl<>(List.of(product));
//        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        Page<ProductDTO> result = productService.getAllProducts("Electronics", "Samsung", null, null, Pageable.unpaged());
//
//        // Since repository returned one and it matches filters, result should contain one
//        assertEquals(1, result.getContent().size());
//        assertEquals("Phone", result.getContent().get(0).getName());
//        verify(productRepository).findAll(any(Pageable.class));
//    }
//
//    // ---------------- updateProduct ----------------
//    @Test
//    void updateProduct_success_updatesAndReturnsDTO() {
//        ProductDTO update = new ProductDTO();
//        update.setName("Updated Phone");
//        update.setDescription("New desc");
//        update.setPrice(600.0);
//        update.setCategory("Electronics");
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        ProductDTO result = productService.updateProduct(1L, update);
//
//        assertNotNull(result);
//        assertEquals("Updated Phone", result.getName());
//        verify(productRepository).save(any(Product.class));
//    }
//
//    @Test
//    void updateProduct_notFound_throws() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDTO));
//    }
//
//    // ---------------- deleteProduct ----------------
//    @Test
//    void deleteProduct_exists_deletes() {
//        when(productRepository.existsById(1L)).thenReturn(true);
//
//        assertDoesNotThrow(() -> productService.deleteProduct(1L));
//        verify(productRepository).deleteById(1L);
//    }
//
//    @Test
//    void deleteProduct_notExists_throws() {
//        when(productRepository.existsById(1L)).thenReturn(false);
//        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
//    }
//
//    // ---------------- updateStock ----------------
//    @Test
//    void updateStock_increase_logsAndReturns() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        // auth principal is UserDetails -> username
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn("Dealer");
//        when(userRepository.findByName("Dealer")).thenReturn(Optional.of(user));
//
//        SecurityContextHolder.setContext(securityContext);
//
//        ProductDTO result = productService.updateStock(1L, 5);
//
//        assertEquals(15, result.getQuantity());
//        verify(transactionLogService).saveLog(eq(1L), eq(100L), eq("INCREASE"), eq(5));
//    }
//
//    @Test
//    void updateStock_decrease_logsAndReturns() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(userDetails.getUsername()).thenReturn("Dealer");
//        when(userRepository.findByName("Dealer")).thenReturn(Optional.of(user));
//        SecurityContextHolder.setContext(securityContext);
//
//        ProductDTO result = productService.updateStock(1L, -2);
//
//        assertEquals(8, result.getQuantity());
//        verify(transactionLogService).saveLog(eq(1L), eq(100L), eq("DECREASE"), eq(2));
//    }
//
//    @Test
//    void updateStock_insufficient_throws() {
//        // product quantity 10 -> try to remove 20
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        assertThrows(ResourceNotFoundException.class, () -> productService.updateStock(1L, -20));
//    }
//
//    @Test
//    void updateStock_productNotFound_throws() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> productService.updateStock(1L, 5));
//    }
//
//    // ---------------- getLowStockProducts ----------------
//    @Test
//    void getLowStockProducts_returnsOnlyLow() {
//        Product low = new Product();
//        low.setId(2L);
//        low.setName("Small");
//        low.setQuantity(1);
//        low.setMinStockLevel(5);
//
//        when(productRepository.findAll()).thenReturn(List.of(product, low));
//
//        List<ProductDTO> lowList = productService.getLowStockProducts();
//
//        assertEquals(1, lowList.size());
//        assertEquals("Small", lowList.get(0).getName());
//    }
//}