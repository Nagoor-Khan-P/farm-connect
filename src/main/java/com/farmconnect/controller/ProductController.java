package com.farmconnect.controller;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.ProductService;
import com.farmconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User farmer = userRepository.findById(userDetails.getId()).orElse(null); // Should not be null if authenticated

        return ResponseEntity.ok(productService.addProduct(product, farmer));
    }
}
