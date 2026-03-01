package com.farmconnect.service;

import com.farmconnect.payload.request.AddressRequest;
import com.farmconnect.payload.response.AddressResponse;
import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse addAddress(UUID userId, AddressRequest addressRequest);

    List<AddressResponse> getUserAddresses(UUID userId);

    AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest addressRequest);

    void deleteAddress(UUID userId, UUID addressId);

    AddressResponse setDefaultAddress(UUID userId, UUID addressId);
}
