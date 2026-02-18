package com.farmconnect.service.impl;

import com.farmconnect.exception.OutOfStockException;
import com.farmconnect.model.Order;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.repository.OrderRepository;
import com.farmconnect.repository.ProductRepository;
import com.farmconnect.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Long productId, int quantity, User buyer) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new com.farmconnect.exception.ResourceNotFoundException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock for product: " + product.getName());
        }

        // Deduct stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // Create Order
        Order order = Order.builder()
                .product(product)
                .buyer(buyer)
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity)
                .orderDate(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }
}
