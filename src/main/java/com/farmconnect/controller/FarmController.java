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

    @PostMapping("/register")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmResponse> registerFarm(
            @Valid @RequestBody FarmRequest farmRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User farmer = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return ResponseEntity.ok(farmService.registerFarm(farmRequest, farmer));
    }

    @GetMapping("/my-farm")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmResponse> getMyFarm(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(farmService.getFarmByFarmer(userDetails.getId()));
    }

    @PutMapping("/my-farm")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmResponse> updateMyFarm(
            @Valid @RequestBody FarmRequest farmRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(farmService.updateFarm(farmRequest, userDetails.getId()));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<FarmResponse> getFarmByFarmer(@PathVariable UUID farmerId) {
        return ResponseEntity.ok(farmService.getFarmByFarmer(farmerId));
    }
}
