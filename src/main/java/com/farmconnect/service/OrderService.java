package com.farmconnect.service;

import com.farmconnect.model.Order;
import com.farmconnect.model.User;
import com.farmconnect.repository.OrderItemRepository;
import com.farmconnect.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<Order> getActiveOrders(UUID buyerId);

    List<Order> getOrderHistory(UUID buyerId);

    Order cancelOrder(UUID orderId);

    Order createOrderFromCart(User buyer, com.farmconnect.payload.request.CheckoutRequest checkoutRequest);

    List<com.farmconnect.model.OrderItem> getSalesByFarmer(UUID farmerId);

    Order updateOrderItemStatus(UUID orderItemId, com.farmconnect.model.OrderStatus status);
}
