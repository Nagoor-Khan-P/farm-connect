package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Product> getProductsByFarmer(Long farmerId) {
        return productRepository.findByFarmerId(farmerId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(Long id, Product product, Long farmerId) {
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
    public void updateStock(Long id, int quantity, Long farmerId) {
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
    public void deleteProduct(Long id, Long farmerId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existingProduct.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        productRepository.delete(existingProduct);
    }
}
