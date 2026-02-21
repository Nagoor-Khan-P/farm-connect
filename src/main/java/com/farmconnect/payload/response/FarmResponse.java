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
public class FarmResponse {
    private UUID id;
    private String name;
    private String address;
    private String description;
    private String farmerUsername;
}
