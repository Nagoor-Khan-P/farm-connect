package com.farmconnect.service.impl;

import com.farmconnect.exception.OutOfStockException;
import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.model.Order;
import com.farmconnect.model.OrderItem;
import com.farmconnect.model.OrderStatus;
import com.farmconnect.model.Product;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.OrderItemRequest;
import com.farmconnect.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
    private final com.farmconnect.repository.AddressRepository addressRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
            ProductService productService, com.farmconnect.repository.CartRepository cartRepository,
            OrderItemRepository orderItemRepository, com.farmconnect.repository.AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
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
    public Order createOrderFromCart(User buyer, com.farmconnect.payload.request.CheckoutRequest checkoutRequest) {
        com.farmconnect.model.Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Set shipping address
        setShippingAddress(order, buyer, checkoutRequest.getAddressId(), null);

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

    private void setShippingAddress(Order order, User buyer, UUID addressId,
            com.farmconnect.payload.request.AddressRequest newAddress) {
        com.farmconnect.model.Address shippingAddress;

        if (addressId != null) {
            com.farmconnect.model.SavedAddress saved = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Saved address not found"));
            if (!saved.getUser().getId().equals(buyer.getId())) {
                throw new RuntimeException("Unauthorized to use this address");
            }
            shippingAddress = saved.getAddress();
        } else if (newAddress != null) {
            shippingAddress = com.farmconnect.model.Address.builder()
                    .street(newAddress.getStreet())
                    .city(newAddress.getCity())
                    .state(newAddress.getState())
                    .zipCode(newAddress.getZipCode())
                    .country(newAddress.getCountry())
                    .build();
        } else {
            throw new IllegalArgumentException("Shipping address is required");
        }

        order.setShippingAddress(shippingAddress);
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

    @Override
    public List<OrderItem> getSalesByFarmer(UUID farmerId) {
        return orderItemRepository.findByProductFarmFarmerId(farmerId);
    }
}
