package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Farm;
import com.farmconnect.model.User;
import com.farmconnect.repository.FarmRepository;
import com.farmconnect.service.FarmService;
import com.farmconnect.payload.request.FarmRequest;
import com.farmconnect.payload.response.FarmResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;

    public FarmServiceImpl(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @Override
    public FarmResponse registerFarm(FarmRequest farmRequest, User farmer) {
        if (farmRepository.existsByFarmerId(farmer.getId())) {
            throw new UnauthorizedActionException("Farmer already has a registered farm");
        }

        Farm farm = Farm.builder()
                .name(farmRequest.getName())
                .address(farmRequest.getAddress())
                .description(farmRequest.getDescription())
                .farmer(farmer)
                .build();

        Farm savedFarm = farmRepository.save(farm);
        return mapToResponse(savedFarm);
    }

    @Override
    public FarmResponse getFarmByFarmer(UUID farmerId) {
        Farm farm = farmRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found for this farmer"));
        return mapToResponse(farm);
    }

    @Override
    public FarmResponse updateFarm(FarmRequest farmRequest, UUID farmerId) {
        Farm farm = farmRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found for this farmer"));

        farm.setName(farmRequest.getName());
        farm.setAddress(farmRequest.getAddress());
        farm.setDescription(farmRequest.getDescription());

        Farm updatedFarm = farmRepository.save(farm);
        return mapToResponse(updatedFarm);
    }

    @Override
    public boolean hasFarm(UUID farmerId) {
        return farmRepository.existsByFarmerId(farmerId);
    }

    private FarmResponse mapToResponse(Farm farm) {
        return FarmResponse.builder()
                .id(farm.getId())
                .name(farm.getName())
                .address(farm.getAddress())
                .description(farm.getDescription())
                .farmerUsername(farm.getFarmer().getUsername())
                .build();
    }
}
