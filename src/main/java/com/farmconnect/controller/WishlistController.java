package com.farmconnect.controller;

import com.farmconnect.payload.response.CartResponse;
import com.farmconnect.payload.response.MessageResponse;
import com.farmconnect.payload.response.WishlistResponse;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(userDetails.getId(), productId));
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<WishlistResponse> getWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getId()));
    }

    @DeleteMapping("/remove/{wishlistItemId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<MessageResponse> removeFromWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID wishlistItemId) {
        wishlistService.removeFromWishlist(userDetails.getId(), wishlistItemId);
        return ResponseEntity.ok(new MessageResponse("Item removed from wishlist"));
    }

    @PostMapping("/move-to-cart/{wishlistItemId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CartResponse> moveToCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID wishlistItemId) {
        return ResponseEntity.ok(wishlistService.moveToCart(userDetails.getId(), wishlistItemId));
    }
}
