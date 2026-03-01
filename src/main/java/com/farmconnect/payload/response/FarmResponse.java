package com.farmconnect.payload.response;

import com.farmconnect.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponse {
    private UUID id;
    private String name;
    private Address address;
    private String description;
    private String farmerUsername;
    private String imageUrl;
    private double averageRating;
    private int ratingCount;
}
