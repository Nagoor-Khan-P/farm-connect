package com.farmconnect.controller;

import com.farmconnect.model.User;
import com.farmconnect.payload.request.FarmRequest;
import com.farmconnect.payload.response.FarmResponse;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.FarmService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;
    private final UserRepository userRepository;

    public FarmController(FarmService farmService, UserRepository userRepository) {
        this.farmService = farmService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/register", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmResponse> registerFarm(
            @RequestPart("farm") @Valid FarmRequest farmRequest,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User farmer = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return ResponseEntity.ok(farmService.registerFarm(farmRequest, farmer, image));
    }

    @GetMapping("/my-farms")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<java.util.List<FarmResponse>> getMyFarms(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(farmService.getFarmsByFarmer(userDetails.getId()));
    }

    @PutMapping(value = "/{farmId}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmResponse> updateMyFarm(
            @PathVariable UUID farmId,
            @RequestPart("farm") @Valid FarmRequest farmRequest,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(farmService.updateFarm(farmId, farmRequest, userDetails.getId(), image));
    }

    @DeleteMapping("/{farmId}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> deleteMyFarm(
            @PathVariable UUID farmId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        farmService.deleteFarm(farmId, userDetails.getId());
        return ResponseEntity.ok(new com.farmconnect.payload.response.MessageResponse("Farm deleted successfully!"));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<FarmResponse>> getFarmsByFarmer(@PathVariable UUID farmerId) {
        return ResponseEntity.ok(farmService.getFarmsByFarmer(farmerId));
    }
}
