package com.farmconnect.controller;

import com.farmconnect.payload.request.OrderItemRequest;
import com.farmconnect.payload.response.CartResponse;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrderItemRequest addToCartRequest) {
        CartResponse cart = cartService.addToCart(userDetails.getId(), addToCartRequest);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        CartResponse cart = cartService.getCart(userDetails.getId());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID cartItemId) {
        cartService.removeFromCart(userDetails.getId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decrease/{cartItemId}")
    public ResponseEntity<CartResponse> decreaseQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID cartItemId) {
        System.out.println("Decreasing quantity for item: " + cartItemId);
        CartResponse cart = cartService.decreaseQuantity(userDetails.getId(), cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
