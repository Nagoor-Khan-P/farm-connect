package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final com.farmconnect.service.FarmService farmService;

    public ProductServiceImpl(ProductRepository productRepository, com.farmconnect.service.FarmService farmService) {
        this.productRepository = productRepository;
        this.farmService = farmService;
    }

    @Override
    public Product addProduct(Product product, User farmer) {
        if (!farmService.hasFarm(farmer.getId())) {
            throw new UnauthorizedActionException("You must register your farm details before adding products");
        }
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByFarmer(UUID farmerId) {
        return productRepository.findByFarmerId(farmerId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(UUID id, Product product, UUID farmerId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existingProduct.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setQuantity(product.getQuantity());

        return productRepository.save(existingProduct);
    }

    @Override
    public void updateStock(UUID id, int quantity, UUID farmerId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existingProduct.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
        productRepository.save(existingProduct);
    }

    @Override
    public void restoreStock(UUID id, int quantity) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
        productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(UUID id, UUID farmerId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existingProduct.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        productRepository.delete(existingProduct);
    }
}
