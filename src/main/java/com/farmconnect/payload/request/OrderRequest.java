package com.farmconnect.payload.request;

import lombok.Data;

@Data
public class OrderRequest {
    private java.util.List<OrderItemRequest> items;
}
