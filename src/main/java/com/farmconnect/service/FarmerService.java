package com.farmconnect.service;

import com.farmconnect.payload.response.FarmerResponse;

import java.util.List;
import java.util.UUID;

public interface FarmerService {
    List<FarmerResponse> getAllFarmers();

    FarmerResponse getFarmerById(UUID id);
}
