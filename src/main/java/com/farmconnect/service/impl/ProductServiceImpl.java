package com.farmconnect.service.impl;

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
}
