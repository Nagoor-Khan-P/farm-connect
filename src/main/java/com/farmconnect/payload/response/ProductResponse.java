package com.farmconnect.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String category;
    private String description;
    private double price;
    private String unit;
    private int quantity;
    private String imageUrl;
    private UUID farmId;
    private String farmName;
    private String farmerName;
}
