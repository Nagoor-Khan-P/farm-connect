package com.farmconnect.payload.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class WishlistResponse {
    private UUID id;
    private List<WishlistItemResponse> items;
}
