package com.farmconnect.payload.request;

import java.util.UUID;
import lombok.Data;

@Data
public class OrderItemRequest {
    private UUID productId;
    private int quantity;
}
