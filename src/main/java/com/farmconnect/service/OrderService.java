package com.farmconnect.service;

import com.farmconnect.model.Order;
import com.farmconnect.model.User;
import com.farmconnect.repository.OrderItemRepository;
import com.farmconnect.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(com.farmconnect.payload.request.OrderRequest orderRequest, User buyer);

    List<Order> getOrdersByBuyer(UUID buyerId);

    Order cancelOrder(UUID orderId);

    Order createOrderFromCart(User buyer);

    java.util.List<com.farmconnect.model.OrderItem> getSalesByFarmer(java.util.UUID farmerId);
}
