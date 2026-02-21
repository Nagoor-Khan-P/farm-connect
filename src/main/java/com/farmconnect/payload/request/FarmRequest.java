package com.farmconnect.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FarmRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String description;
}
