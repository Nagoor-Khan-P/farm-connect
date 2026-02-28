package com.farmconnect.controller;

import com.farmconnect.model.User;
import com.farmconnect.payload.request.ProductRequest;
import com.farmconnect.payload.response.ProductResponse;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.ProductService;
import org.springframework.web.multipart.MultipartFile;
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
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public List<ProductResponse> getMyProducts(@RequestParam(required = false) UUID farmId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (farmId != null) {
            return productService.getProductsByFarm(farmId);
        }
        return productService.getProductsByFarmer(userDetails.getId());
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart("product") @jakarta.validation.Valid ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User farmer = userRepository.findById(userDetails.getId()).orElse(null);

        return ResponseEntity.ok(productService.addProduct(productRequest, image, farmer));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('ROLE_FARMER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestPart("product") @jakarta.validation.Valid ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(productService.updateProduct(id, productRequest, userDetails.getId(), image));
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
