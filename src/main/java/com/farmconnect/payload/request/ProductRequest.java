package com.farmconnect.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @Positive
    private double price;

    @NotBlank
    private String unit;

    @PositiveOrZero
    private int quantity;

    private String description;

    private java.util.UUID farmId;
}
