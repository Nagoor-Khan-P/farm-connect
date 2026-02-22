package com.farmconnect.payload.request;

import com.farmconnect.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FarmRequest {
    @NotBlank
    private String name;

    @NotNull
    private Address address;

    private String description;
}
