package com.farmconnect.service.impl;

import com.farmconnect.model.Rating;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.RatingRequest;
import com.farmconnect.payload.response.RatingResponse;
import com.farmconnect.repository.RatingRepository;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.service.RatingService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RatingResponse submitRating(RatingRequest ratingRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        Rating rating = Rating.builder()
                .user(user)
                .targetId(ratingRequest.getTargetId())
                .targetType(ratingRequest.getTargetType())
                .score(ratingRequest.getScore())
                .comment(ratingRequest.getComment())
                .createdAt(Instant.now())
                .build();

        Rating savedRating = ratingRepository.save(rating);
        return mapToResponse(savedRating);
    }

    @Override
    public List<RatingResponse> getRatingsForTarget(UUID targetId, Rating.TargetType targetType) {
        return ratingRepository.findByTargetIdAndTargetType(targetId, targetType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(UUID targetId, Rating.TargetType targetType) {
        Double avg = ratingRepository.findAverageScoreByTarget(targetId, targetType);
        return avg != null ? avg : 0.0;
    }

    @Override
    public Long getRatingCount(UUID targetId, Rating.TargetType targetType) {
        return ratingRepository.countByTarget(targetId, targetType);
    }

    private RatingResponse mapToResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .username(rating.getUser().getUsername())
                .targetId(rating.getTargetId())
                .targetType(rating.getTargetType())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
