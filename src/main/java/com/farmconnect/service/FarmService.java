package com.farmconnect.service;

import com.farmconnect.model.User;
import com.farmconnect.payload.request.FarmRequest;
import com.farmconnect.payload.response.FarmResponse;

import java.util.List;
import java.util.UUID;

public interface FarmService {
    FarmResponse registerFarm(FarmRequest farmRequest, User farmer,
            org.springframework.web.multipart.MultipartFile image);

    List<FarmResponse> getFarmsByFarmer(UUID farmerId);

    FarmResponse updateFarm(UUID farmId, FarmRequest farmRequest, UUID farmerId,
            org.springframework.web.multipart.MultipartFile image);

    void deleteFarm(UUID farmId, UUID farmerId);

    boolean hasFarm(UUID farmerId);
}
