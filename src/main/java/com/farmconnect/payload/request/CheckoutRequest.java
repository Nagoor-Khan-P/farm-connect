package com.farmconnect.payload.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CheckoutRequest {
    private UUID addressId;
}
