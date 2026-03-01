package com.farmconnect.repository;

import com.farmconnect.model.SavedAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<SavedAddress, UUID> {
    List<SavedAddress> findByUserId(UUID userId);
}
