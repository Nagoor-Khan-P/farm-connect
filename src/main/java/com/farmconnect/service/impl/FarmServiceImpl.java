package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.exception.UnauthorizedActionException;
import com.farmconnect.model.Address;
import com.farmconnect.model.Farm;
import com.farmconnect.model.User;
import com.farmconnect.repository.FarmRepository;
import com.farmconnect.service.FarmService;
import com.farmconnect.payload.request.FarmRequest;
import com.farmconnect.payload.response.FarmResponse;
import com.farmconnect.service.FileStorageService;
import com.farmconnect.service.RatingService;
import com.farmconnect.model.Rating;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FileStorageService fileStorageService;
    private final RatingService ratingService;

    public FarmServiceImpl(FarmRepository farmRepository, FileStorageService fileStorageService,
            RatingService ratingService) {
        this.farmRepository = farmRepository;
        this.fileStorageService = fileStorageService;
        this.ratingService = ratingService;
    }

    @Override
    public FarmResponse registerFarm(FarmRequest farmRequest, User farmer, MultipartFile image) {
        // Removed check for single farm to allow multiple farms
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.saveFile(image, "farms");
        }

        Farm farm = Farm.builder()
                .name(farmRequest.getName())
                .address(farmRequest.getAddress())
                .description(farmRequest.getDescription())
                .farmer(farmer)
                .imageUrl(imageUrl)
                .build();

        Farm savedFarm = farmRepository.save(farm);
        return mapToResponse(savedFarm);
    }

    @Override
    public List<FarmResponse> getFarmsByFarmer(UUID farmerId) {
        return farmRepository.findByFarmerId(farmerId).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public FarmResponse updateFarm(UUID farmId, FarmRequest farmRequest, UUID farmerId, MultipartFile image) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException("You are not authorized to update this farm");
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(image, "farms");
            farm.setImageUrl(imageUrl);
        }

        farm.setName(farmRequest.getName());
        farm.setAddress(farmRequest.getAddress());
        farm.setDescription(farmRequest.getDescription());

        Farm updatedFarm = farmRepository.save(farm);
        return mapToResponse(updatedFarm);
    }

    @Override
    public void deleteFarm(UUID farmId, UUID farmerId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedActionException("You are not authorized to delete this farm");
        }

        farmRepository.delete(farm);
    }

    @Override
    public boolean hasFarm(UUID farmerId) {
        return !farmRepository.findByFarmerId(farmerId).isEmpty();
    }

    private FarmResponse mapToResponse(Farm farm) {
        return FarmResponse.builder()
                .id(farm.getId())
                .name(farm.getName())
                .address(farm.getAddress())
                .description(farm.getDescription())
                .farmerUsername(farm.getFarmer().getUsername())
                .imageUrl(farm.getImageUrl())
                .averageRating(ratingService.getAverageRating(farm.getId(), Rating.TargetType.FARM))
                .ratingCount(ratingService.getRatingCount(farm.getId(), Rating.TargetType.FARM).intValue())
                .build();
    }
}
