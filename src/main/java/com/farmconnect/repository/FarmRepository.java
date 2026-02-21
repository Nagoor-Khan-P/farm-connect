package com.farmconnect.repository;

import com.farmconnect.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmRepository extends JpaRepository<Farm, UUID> {
    Optional<Farm> findByFarmerId(UUID farmerId);

    boolean existsByFarmerId(UUID farmerId);
}
