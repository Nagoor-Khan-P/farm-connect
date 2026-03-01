package com.farmconnect.repository;

import com.farmconnect.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmconnect.model.OrderStatus;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByBuyerId(UUID buyerId);

    List<Order> findByBuyerIdAndStatusIn(UUID buyerId, List<OrderStatus> statuses);
}
