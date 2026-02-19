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

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(Product product, User farmer) {
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
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity()); // Can also update quantity here

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
