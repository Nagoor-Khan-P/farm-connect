package com.farmconnect.payload.response;

import lombok.Data;
import java.util.UUID;

@Data
public class AddressResponse {
    private UUID id;
    private String label;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;
}
