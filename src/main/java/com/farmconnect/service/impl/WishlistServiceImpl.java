package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.model.*;
import com.farmconnect.payload.request.OrderItemRequest;
import com.farmconnect.payload.response.CartResponse;
import com.farmconnect.payload.response.WishlistItemResponse;
import com.farmconnect.payload.response.WishlistResponse;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.repository.WishlistItemRepository;
import com.farmconnect.repository.WishlistRepository;
import com.farmconnect.service.CartService;
import com.farmconnect.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(UUID userId, UUID productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(
                        () -> wishlistRepository.save(Wishlist.builder().user(user).items(new ArrayList<>()).build()));

        boolean exists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (!exists) {
            WishlistItem newItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .build();
            wishlist.getItems().add(newItem);
            wishlistRepository.save(wishlist);
        }

        return mapToResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse getWishlist(UUID userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return wishlistRepository.save(Wishlist.builder().user(user).items(new ArrayList<>()).build());
                });
        return mapToResponse(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID wishlistItemId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem itemToRemove = wishlist.getItems().stream()
                .filter(item -> item.getId().equals(wishlistItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        wishlist.getItems().remove(itemToRemove);
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public CartResponse moveToCart(UUID userId, UUID wishlistItemId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem itemToMove = wishlist.getItems().stream()
                .filter(item -> item.getId().equals(wishlistItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        Product product = itemToMove.getProduct();

        // Add to cart with quantity 1
        OrderItemRequest addToCartRequest = new OrderItemRequest();
        addToCartRequest.setProductId(product.getId());
        addToCartRequest.setQuantity(1);

        CartResponse cartResponse = cartService.addToCart(userId, addToCartRequest);

        // Remove from wishlist
        wishlist.getItems().remove(itemToMove);
        wishlistRepository.save(wishlist);

        return cartResponse;
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlist.getId());
        response.setItems(wishlist.getItems().stream().map(this::mapItemToResponse).collect(Collectors.toList()));
        return response;
    }

    private WishlistItemResponse mapItemToResponse(WishlistItem item) {
        WishlistItemResponse response = new WishlistItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setCategory(item.getProduct().getCategory());
        response.setPrice(item.getProduct().getPrice());
        response.setImageUrl(item.getProduct().getImageUrl());
        if (item.getProduct().getFarm() != null) {
            response.setFarmName(item.getProduct().getFarm().getName());
            if (item.getProduct().getFarm().getFarmer() != null) {
                User farmer = item.getProduct().getFarm().getFarmer();
                response.setFarmerName(farmer.getFirstName() + " " + farmer.getLastName());
            }
        }
        return response;
    }
}
