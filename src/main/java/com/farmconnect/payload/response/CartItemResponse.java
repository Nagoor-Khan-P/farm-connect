package com.farmconnect.payload.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private int quantity;
    private double price;
    private double subTotal;
}
