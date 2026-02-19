package com.farmconnect.service;

import com.farmconnect.model.Order;
import com.farmconnect.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(com.farmconnect.payload.request.OrderRequest orderRequest, User buyer);

    List<Order> getOrdersByBuyer(Long buyerId);
}
