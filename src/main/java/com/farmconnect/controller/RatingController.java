package com.farmconnect.controller;

import com.farmconnect.model.Rating;
import com.farmconnect.payload.request.RatingRequest;
import com.farmconnect.payload.response.RatingResponse;
import com.farmconnect.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BUYER') or hasRole('FARMER')")
    public ResponseEntity<RatingResponse> submitRating(@Valid @RequestBody RatingRequest ratingRequest) {
        return ResponseEntity.ok(ratingService.submitRating(ratingRequest));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<List<RatingResponse>> getRatings(
            @PathVariable Rating.TargetType targetType,
            @PathVariable UUID targetId) {
        return ResponseEntity.ok(ratingService.getRatingsForTarget(targetId, targetType));
    }

    @GetMapping("/{targetType}/{targetId}/average")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable Rating.TargetType targetType,
            @PathVariable UUID targetId) {
        return ResponseEntity.ok(ratingService.getAverageRating(targetId, targetType));
    }
}
