package com.farmconnect.payload.response;

import lombok.Data;
import java.util.UUID;

@Data
public class WishlistItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String category;
    private double price;
    private String imageUrl;
    private String farmName;
    private String farmerName;
}
