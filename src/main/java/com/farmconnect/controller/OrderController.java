package com.farmconnect.controller;

import com.farmconnect.model.Order;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.OrderRequest;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.OrderService;
import com.farmconnect.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    public List<Order> getMyOrders() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return orderService.getOrdersByBuyer(userDetails.getId());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User buyer = userRepository.findById(userDetails.getId()).orElse(null);

        try {
            Order order = orderService.createOrder(orderRequest, buyer);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
