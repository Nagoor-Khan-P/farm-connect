package com.farmconnect.service;

import com.farmconnect.model.User;
import com.farmconnect.payload.request.ProductRequest;
import com.farmconnect.payload.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest, MultipartFile image, User farmer);

    List<ProductResponse> getProductsByFarm(UUID farmId);

    List<ProductResponse> getProductsByFarmer(UUID farmerId);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse updateProduct(UUID id, ProductRequest productRequest, UUID farmerId, MultipartFile image);

    void updateStock(UUID id, int quantity, UUID farmerId);

    void restoreStock(UUID id, int quantity);

    void deleteProduct(UUID id, UUID farmerId);
}
