package com.farmconnect.service;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product addProduct(Product product, User farmer);

    List<Product> getProductsByFarmer(UUID farmerId);

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    Product updateProduct(UUID id, Product product, UUID farmerId);

    void updateStock(UUID id, int quantity, UUID farmerId);

    void deleteProduct(UUID id, UUID farmerId);
}
