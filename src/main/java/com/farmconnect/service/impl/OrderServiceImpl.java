package com.farmconnect.service.impl;

import com.farmconnect.exception.OutOfStockException;
import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.model.Order;
import com.farmconnect.model.OrderItem;
import com.farmconnect.model.OrderStatus;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.OrderItemRequest;
import com.farmconnect.payload.request.OrderRequest;
import com.farmconnect.repository.OrderRepository;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.service.OrderService;
import com.farmconnect.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
            ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest, User buyer) {
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalOrderPrice = 0;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with ID: " + itemRequest.getProductId()));

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new OutOfStockException("Not enough stock for product: " + product.getName());
            }

            // Deduct stock
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Create OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalOrderPrice += product.getPrice() * itemRequest.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByBuyer(UUID buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    @Override
    public Order cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that has been shipped or delivered");
        }

        order.setStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            productService.restoreStock(item.getProduct().getId(), item.getQuantity());
        }

        return orderRepository.save(order);
    }
}
