package com.farmconnect.service;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.model.Farm;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product addProduct(Product product, java.util.UUID farmId, User farmer);

    List<Product> getProductsByFarm(UUID farmId);

    List<Product> getProductsByFarmer(UUID farmerId);

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    Product updateProduct(UUID id, Product product, UUID farmerId);

    void updateStock(UUID id, int quantity, UUID farmerId);

    void restoreStock(UUID id, int quantity);

    void deleteProduct(UUID id, UUID farmerId);
}
