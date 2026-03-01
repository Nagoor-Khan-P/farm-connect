package com.farmconnect.service;

import com.farmconnect.model.Rating;
import com.farmconnect.payload.request.RatingRequest;
import com.farmconnect.payload.response.RatingResponse;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    RatingResponse submitRating(RatingRequest ratingRequest);

    List<RatingResponse> getRatingsForTarget(UUID targetId, Rating.TargetType targetType);

    Double getAverageRating(UUID targetId, Rating.TargetType targetType);

    Long getRatingCount(UUID targetId, Rating.TargetType targetType);
}
