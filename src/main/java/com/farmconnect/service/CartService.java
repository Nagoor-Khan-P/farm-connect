package com.farmconnect.service;

import com.farmconnect.payload.response.CartResponse;
import com.farmconnect.payload.request.OrderItemRequest;
import java.util.UUID;

public interface CartService {
    CartResponse addToCart(UUID userId, OrderItemRequest addToCartRequest);

    CartResponse getCart(UUID userId);

    void removeFromCart(UUID userId, UUID cartItemId);

    void clearCart(UUID userId);
}
