package com.farmconnect.service;

import com.farmconnect.payload.response.WishlistResponse;
import com.farmconnect.payload.response.CartResponse;
import java.util.UUID;

public interface WishlistService {
    WishlistResponse addToWishlist(UUID userId, UUID productId);

    WishlistResponse getWishlist(UUID userId);

    void removeFromWishlist(UUID userId, UUID wishlistItemId);

    CartResponse moveToCart(UUID userId, UUID wishlistItemId);
}
