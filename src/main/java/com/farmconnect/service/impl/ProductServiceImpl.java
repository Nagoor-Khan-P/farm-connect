package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.model.Farm;
import com.farmconnect.repository.FarmRepository;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.payload.request.ProductRequest;
import com.farmconnect.payload.response.ProductResponse;
import com.farmconnect.service.FileStorageService;
import com.farmconnect.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FarmRepository farmRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository, FarmRepository farmRepository,
            FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.farmRepository = farmRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest, MultipartFile image, User farmer) {
        if (productRequest.getFarmId() == null) {
            throw new RuntimeException("Farm ID is required for adding a product");
        }
        Farm farm = farmRepository.findById(productRequest.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmer.getId())) {
            throw new UnauthorizedActionException("You do not own this farm");
        }

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setUnit(productRequest.getUnit());
        product.setQuantity(productRequest.getQuantity());
        product.setFarm(farm);

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(image, "products");
            product.setImageUrl(imageUrl);
        }

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public List<ProductResponse> getProductsByFarm(UUID farmId) {
        return productRepository.findByFarmId(farmId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByFarmer(UUID farmerId) {
        return productRepository.findByFarmFarmerId(farmerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest productRequest, UUID farmerId, MultipartFile image) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existingProduct.getFarm().getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException(
                    "Unauthorized: You are not the owner of this product");
        }

        existingProduct.setName(productRequest.getName());
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setUnit(productRequest.getUnit());
        existingProduct.setQuantity(productRequest.getQuantity());

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(image, "products");
            existingProduct.setImageUrl(imageUrl);
        }

        return mapToResponse(productRepository.save(existingProduct));
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

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setUnit(product.getUnit());
        response.setQuantity(product.getQuantity());
        response.setFarmId(product.getFarm().getId());
        response.setFarmName(product.getFarm().getName());
        User farmer = product.getFarm().getFarmer();
        response.setFarmerName(farmer.getFirstName() + " " + farmer.getLastName());
        response.setImageUrl(product.getImageUrl());
        return response;
    }
}
