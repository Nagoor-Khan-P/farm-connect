package com.farmconnect.service;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;

import java.util.List;

public interface ProductService {
    Product addProduct(Product product, User farmer);

    List<Product> getProductsByFarmer(Long farmerId);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product, Long farmerId);

    void updateStock(Long id, int quantity, Long farmerId);

    void deleteProduct(Long id, Long farmerId);
}
