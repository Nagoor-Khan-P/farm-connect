package com.farmconnect.controller;

import com.farmconnect.payload.response.FarmResponse;
import com.farmconnect.payload.response.FarmerResponse;
import com.farmconnect.payload.response.ProductResponse;
import com.farmconnect.service.FarmService;
import com.farmconnect.service.FarmerService;
import com.farmconnect.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/farmers")
public class FarmerController {

    private final FarmerService farmerService;
    private final FarmService farmService;
    private final ProductService productService;

    public FarmerController(FarmerService farmerService, FarmService farmService, ProductService productService) {
        this.farmerService = farmerService;
        this.farmService = farmService;
        this.productService = productService;
    }

    @GetMapping
    public List<FarmerResponse> getAllFarmers() {
        return farmerService.getAllFarmers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerResponse> getFarmerById(@PathVariable UUID id) {
        return ResponseEntity.ok(farmerService.getFarmerById(id));
    }

    @GetMapping("/{id}/farms")
    public List<FarmResponse> getFarmsByFarmer(@PathVariable UUID id) {
        return farmService.getFarmsByFarmer(id);
    }

    @GetMapping("/{id}/products")
    public List<ProductResponse> getProductsByFarmer(@PathVariable UUID id) {
        return productService.getProductsByFarmer(id);
    }
}
