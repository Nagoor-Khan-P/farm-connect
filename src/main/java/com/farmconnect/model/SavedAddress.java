package com.farmconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_addresses")
public class SavedAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String label; // e.g., Home, Work

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean isDefault;
}
