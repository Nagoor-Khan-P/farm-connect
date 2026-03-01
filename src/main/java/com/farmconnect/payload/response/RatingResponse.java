package com.farmconnect.payload.response;

import com.farmconnect.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private UUID id;
    private String username;
    private UUID targetId;
    private Rating.TargetType targetType;
    private int score;
    private String comment;
    private Instant createdAt;
}
