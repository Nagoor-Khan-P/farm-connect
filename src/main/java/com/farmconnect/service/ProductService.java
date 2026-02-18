package com.farmconnect.service;

import com.farmconnect.model.Product;
import com.farmconnect.model.User;

import java.util.List;

public interface ProductService {
    Product addProduct(Product product, User farmer);

    List<Product> getProductsByFarmer(Long farmerId);

    List<Product> getAllProducts();

    Product getProductById(Long id);
}
