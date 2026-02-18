package com.farmconnect.payload.request;

import lombok.Data;

@Data
public class OrderRequest {
    private Long productId;
    private int quantity;
}
