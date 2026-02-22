package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.model.Farm;
import com.farmconnect.repository.FarmRepository;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FarmRepository farmRepository;

    public ProductServiceImpl(ProductRepository productRepository, FarmRepository farmRepository) {
        this.productRepository = productRepository;
        this.farmRepository = farmRepository;
    }

    @Override
    public Product addProduct(Product product, java.util.UUID farmId, User farmer) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmer.getId())) {
            throw new UnauthorizedActionException("You do not own this farm");
        }

        product.setFarm(farm);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByFarm(UUID farmId) {
        return productRepository.findByFarmId(farmId);
    }

    @Override
    public List<Product> getProductsByFarmer(UUID farmerId) {
        return productRepository.findByFarmFarmerId(farmerId);
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

        if (!existingProduct.getFarm().getFarmer().getId().equals(farmerId)) {
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

        if (!existingProduct.getFarm().getFarmer().getId().equals(farmerId)) {
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

        if (!existingProduct.getFarm().getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        productRepository.delete(existingProduct);
    }
}
