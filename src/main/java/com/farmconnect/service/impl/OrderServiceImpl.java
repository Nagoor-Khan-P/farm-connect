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
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final com.farmconnect.repository.CartRepository cartRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
            ProductService productService, com.farmconnect.repository.CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.cartRepository = cartRepository;
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

            OrderItem orderItem = processOrderItem(order, product, itemRequest.getQuantity(), product.getPrice());
            orderItems.add(orderItem);
            totalOrderPrice += orderItem.getPrice() * orderItem.getQuantity();
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

    @Override
    @Transactional
    public Order createOrderFromCart(User buyer) {
        com.farmconnect.model.Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalOrderPrice = 0;

        for (com.farmconnect.model.CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = processOrderItem(order, cartItem.getProduct(), cartItem.getQuantity(),
                    cartItem.getPrice());
            orderItems.add(orderItem);
            totalOrderPrice += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

        return savedOrder;
    }

    private OrderItem processOrderItem(Order order, Product product, int quantity, double price) {
        if (product.getQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock for product: " + product.getName());
        }

        // Deduct stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
