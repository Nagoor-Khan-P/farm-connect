package com.farmconnect.service;

import com.farmconnect.model.Order;
import com.farmconnect.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(Long productId, int quantity, User buyer);

    List<Order> getOrdersByBuyer(Long buyerId);
}
