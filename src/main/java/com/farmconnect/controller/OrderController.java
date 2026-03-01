package com.farmconnect.controller;

import com.farmconnect.model.Order;
import com.farmconnect.model.OrderItem;
import com.farmconnect.model.User;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.OrderService;
import com.farmconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('FARMER') or hasRole('BUYER')")
    public List<Order> getActiveOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return orderService.getActiveOrders(userDetails.getId());
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('FARMER') or hasRole('BUYER')")
    public List<Order> getOrderHistory() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return orderService.getOrderHistory(userDetails.getId());
    }

    @GetMapping("/my-sales")
    @PreAuthorize("hasRole('FARMER')")
    public List<OrderItem> getMySales() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return orderService.getSalesByFarmer(userDetails.getId());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('FARMER') or hasRole('BUYER')")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID id) {
        Order canceledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(canceledOrder);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    public ResponseEntity<?> checkout(@RequestBody com.farmconnect.payload.request.CheckoutRequest checkoutRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User buyer = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Order order = orderService.createOrderFromCart(buyer, checkoutRequest);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
