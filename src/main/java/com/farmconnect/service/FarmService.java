package com.farmconnect.service;

import com.farmconnect.model.User;
import com.farmconnect.payload.request.FarmRequest;
import com.farmconnect.payload.response.FarmResponse;

import java.util.UUID;

public interface FarmService {
    FarmResponse registerFarm(FarmRequest farmRequest, User farmer);

    FarmResponse getFarmByFarmer(UUID farmerId);

    FarmResponse updateFarm(FarmRequest farmRequest, UUID farmerId);

    boolean hasFarm(UUID farmerId);
}
