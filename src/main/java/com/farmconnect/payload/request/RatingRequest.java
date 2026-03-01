package com.farmconnect.payload.request;

import com.farmconnect.model.Rating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RatingRequest {
    @NotNull
    private UUID targetId;

    @NotNull
    private Rating.TargetType targetType;

    @Min(1)
    @Max(5)
    private int score;

    private String comment;
}
