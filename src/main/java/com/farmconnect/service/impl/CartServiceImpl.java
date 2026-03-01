package com.farmconnect.service.impl;

import com.farmconnect.model.Cart;
import com.farmconnect.model.CartItem;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.OrderItemRequest;
import com.farmconnect.payload.response.CartResponse;
import com.farmconnect.payload.response.CartItemResponse;
import com.farmconnect.repository.CartItemRepository;
import com.farmconnect.repository.CartRepository;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.service.CartService;
import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.OutOfStockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CartResponse addToCart(UUID userId, OrderItemRequest addToCartRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(addToCartRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getQuantity() < addToCartRequest.getQuantity()) {
            throw new OutOfStockException("Product is out of stock");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder().user(user).items(new ArrayList<>()).totalPrice(0.0).build());

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + addToCartRequest.getQuantity());
            item.setPrice(product.getPrice());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addToCartRequest.getQuantity())
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse getCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Cart newCart = Cart.builder().user(user).items(new ArrayList<>()).totalPrice(0.0).build();
                    return cartRepository.save(newCart);
                });
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(UUID userId, UUID cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(itemToRemove);

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse decreaseQuantity(UUID userId, UUID cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cart.getItems().remove(item);
        }

        updateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalPrice(cart.getTotalPrice());
        response.setItems(cart.getItems().stream().map(this::mapItemToResponse).collect(Collectors.toList()));
        return response;
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubTotal(item.getPrice() * item.getQuantity());
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
