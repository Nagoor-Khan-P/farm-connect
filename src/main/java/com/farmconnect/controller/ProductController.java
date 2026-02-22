package com.farmconnect.controller;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.ProductRequest;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.ProductService;
import com.farmconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public List<Product> getMyProducts() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return productService.getProductsByFarmer(userDetails.getId());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<?> addProduct(@jakarta.validation.Valid @RequestBody ProductRequest productRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User farmer = userRepository.findById(userDetails.getId()).orElse(null);

        Product product = Product.builder()
                .name(productRequest.getName())
                .category(productRequest.getCategory())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .unit(productRequest.getUnit())
                .quantity(productRequest.getQuantity())
                .build();

        return ResponseEntity.ok(productService.addProduct(product, farmer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id,
            @jakarta.validation.Valid @RequestBody ProductRequest productRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        Product product = Product.builder()
                .name(productRequest.getName())
                .category(productRequest.getCategory())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .unit(productRequest.getUnit())
                .quantity(productRequest.getQuantity())
                .build();

        return ResponseEntity.ok(productService.updateProduct(id, product, userDetails.getId()));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<?> updateStock(@PathVariable UUID id, @RequestBody int quantity) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        productService.updateStock(id, quantity, userDetails.getId());
        return ResponseEntity.ok("Stock updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        productService.deleteProduct(id, userDetails.getId());
        return ResponseEntity.ok("Product deleted successfully");
    }
}
