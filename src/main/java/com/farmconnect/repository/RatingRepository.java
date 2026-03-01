package com.farmconnect.repository;

import com.farmconnect.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByTargetIdAndTargetType(UUID targetId, Rating.TargetType targetType);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.targetId = :targetId AND r.targetType = :targetType")
    Double findAverageScoreByTarget(UUID targetId, Rating.TargetType targetType);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.targetId = :targetId AND r.targetType = :targetType")
    Long countByTarget(UUID targetId, Rating.TargetType targetType);
}
